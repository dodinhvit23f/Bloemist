package com.bloemist.services.impl;

import static com.bloemist.constant.Constants.ERR_ORDER_INFO_001;
import static com.utils.Utils.currencyToStringNumber;
import static com.utils.Utils.isDate;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

import com.bloemist.converters.OrderMapper;
import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.bloemist.events.MessageLoading;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.repositories.OrderReportRepository;
import com.bloemist.services.IOrderService;
import com.bloemist.constant.Constants;
import com.bloemist.constant.OrderState;
import com.bloemist.services.QrServiceI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.utils.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {

  public static final String BLOEMIST_FOLDER_ID = "1vfhBcn9nJKrqYhKVrN5Mg9AMlDJkzbz0";
  public static final String WEB_VIEW_LINK = "webViewLink";
  public static final String ID = "id";
  public static final String MEDIA = "media";
  public static final String READER = "reader";
  public static final String ANYONE = "anyone";
  public static final String GOOGLE_IMAGE_LINK = "https://drive.google.com/uc?id=%s";

  OrderReportRepository orderReportRepository;
  ApplicationEventPublisher publisher;
  OrderMapper orderMapper;
  Drive googleDrive;
  QrServiceI qrService;
  ObjectMapper objectMapper;

  @Override

  public Optional<Boolean> createNewOrder(Order customerOrder) {

    if (Boolean.FALSE.equals(validOrder(customerOrder))) {
      return Optional.empty();
    }

    Optional<Boolean> result = insertOneOrder(customerOrder, OrderState.PENDING.getState());

    if (result.isEmpty()) {
      publisher.publishEvent(new MessageWarning(Constants.CONNECTION_FAIL));
      return Optional.empty();
    }
    publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_001));
    return result;
  }

  @Override
  public void createNewOrders(Collection<Order> orders) {

    try {

      orders.forEach(customerOrder -> insertOneOrder(customerOrder,
          OrderState.getState(customerOrder.getStatus())));

      publisher.publishEvent(new MessageWarning(Constants.SUSS_ORDER_INFO_001));
    } catch (Exception ex) {
      publisher.publishEvent(new MessageWarning(Constants.CONNECTION_FAIL));
    }
  }

  @Override
  public Optional<Boolean> updateOrder(Order order) {
    if (Boolean.FALSE.equals(validOrder(order))) {
      return Optional.empty();
    }
    var optionalOrderReport = orderReportRepository.findByOrderCode(order.getCode());
    if (optionalOrderReport.isPresent()) {
      try {
        updateFieldsCanChange(order, optionalOrderReport.get());
        order.setImagePath(optionalOrderReport.get().getSamplePictureLink());
      } catch (Exception exception) {
        publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_004));
      }
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_002));
      return Optional.of(Boolean.TRUE);
    }

    return Optional.empty();
  }

  @Override
  public void updateOrders(List<Order> orders) {

    Optional<Order> failOrder = orders.stream()
        .filter(order -> !validOrder(order))
        .findFirst();

    if (failOrder.isPresent()) {
      publisher.publishEvent(new MessageWarning(ERR_ORDER_INFO_001));
      return;
    }

    Map<String, Order> orderMap = orders.stream()
        .collect(Collectors.toMap(Order::getCode, Function.identity()));

    final List<String> codes = orders.stream().map(Order::getCode).toList();

    Map<String, OrderReport> orderReports = orderReportRepository
        .findOrderReportByOrderCodeIn(codes)
        .stream()
        .collect(Collectors.toMap(OrderReport::getOrderCode, Function.identity()));

    codes.forEach(code -> {
      if (orderReports.containsKey(code)) {
        try {
          updateFieldsCanChange(orderMap.get(code), orderReports.get(code));
        } catch (ParseException e) {
          publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_003, code));
          codes.remove(code);
        } catch (IOException e) {
          publisher.publishEvent(new MessageWarning(Constants.CONNECTION_FAIL, code));
          codes.remove(code);
        }
      }
    });

    try {
      orderReportRepository.saveAll(orderReports.values());
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_002));
    } catch (Exception e) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_009));
    }

  }

  @Override
  public void deleteOrder(String orderCode) {
    var optionalOrderReport = orderReportRepository.findByOrderCode(orderCode);

    optionalOrderReport.ifPresentOrElse(orderReport -> {
      orderReport.setOrderStatus(OrderState.CANCEL.getState());
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_003));
    }, () -> publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_004)));

  }

  @Override
  public Optional changeOrderStateInfo(Order order) {
    var optionalOrderReport = orderReportRepository.findByOrderCode(order.getCode());

    if (optionalOrderReport.isPresent()) {
      var orderReport = optionalOrderReport.get();
      orderReport.setOrderStatus(OrderState.getState(order.getStatus()));
      orderReport.setActualDeliveryFee(new BigDecimal(order.getActualDeliveryFee()));
      orderReport.setRemark(order.getCustomerNote());
      publisher.publishEvent(new MessageSuccess(Constants.SUS_ORDER_STATUS));
      return Optional.of(Boolean.TRUE);
    }
    publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_004));

    return Optional.empty();
  }

  @Override
  public List<Order> getStaffPage(LocalDateTime startTime, LocalDateTime endTime) {

    final var startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
    final var endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

    var pageOrderReport = orderReportRepository
        .getOrdersForStaff(startDate, endDate);

    return streamMappingOrderReportToOrder(pageOrderReport.stream());
  }

  @Override
  public List<Order> getAdminPage(LocalDateTime startTime, LocalDateTime endTime) {

    final var startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
    final var endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

    var pageOrderReport = orderReportRepository
        .getOrdersAdmin(startDate, endDate);

    return streamMappingOrderReportToOrder(pageOrderReport.stream());
  }

  @Override
  public boolean validOrder(Order orderInfo) {
    if (ObjectUtils.isEmpty(orderInfo.getCustomerName())
        || ObjectUtils.isEmpty(orderInfo.getCustomerPhone())
        || ObjectUtils.isEmpty(orderInfo.getDeliveryDate())
        || ObjectUtils.isEmpty(orderInfo.getDeliveryAddress())
        || ObjectUtils.isEmpty(orderInfo.getDeliveryHour())
        || ObjectUtils.isEmpty(orderInfo.getImagePath())
        || ObjectUtils.isEmpty(orderInfo.getActualPrice())
        || ObjectUtils.isEmpty(orderInfo.getDeliveryFee())
        || ObjectUtils.isEmpty(orderInfo.getDeposit())
        || ObjectUtils.isEmpty(orderInfo.getRemain())
        || ObjectUtils.isEmpty(orderInfo.getSalePrice())
        || ObjectUtils.isEmpty(orderInfo.getTotal())) {
      publisher.publishEvent(new MessageWarning(ERR_ORDER_INFO_001));
      return Boolean.FALSE;
    }

    if (!Utils.isNumber(orderInfo.getSalePrice())
        || !Utils.isNumber(orderInfo.getDeliveryFee())
        || !Utils.isNumber(orderInfo.getVatFee())
        || !Utils.isNumber(orderInfo.getDeposit())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_002));
      return Boolean.FALSE;
    }

    if (!isDate(orderInfo.getDeliveryDate())) {
      return Boolean.FALSE;
    }

    Pattern pattern = Pattern.compile("\\d{2}:\\d{2} - \\d{2}:\\d{2}");
    if (!pattern.matcher(orderInfo.getDeliveryHour()).matches()) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_003));
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }

  @Override
  public List<Order> searchUserByConditionForStaff(String condition) {

    return streamMappingOrderReportToOrder(
        orderReportRepository.searchOrderReportByConditionForStaff(condition)
            .stream());
  }

  @Override
  public List<Order> searchUserByConditionForAdmin(String condition) {
    return streamMappingOrderReportToOrder(
        orderReportRepository.searchOrderReportByConditionForAdmin(condition)
            .stream());
  }

  private List<Order> streamMappingOrderReportToOrder(Stream<OrderReport> stream) {
    AtomicInteger stt = new AtomicInteger(BigInteger.ONE.intValue());
    return stream.map(orderReport -> {
      var order = orderMapper.orderReportToOrder(orderReport);
      order.setStt(String.valueOf(stt.getAndIncrement()));
      return order;
    }).toList();
  }

  private static String getOrderCode() {
    return String.format("%s%d", Constants.ORDER_CODER_PRE_FIX, System.nanoTime());
  }

  private Optional<Boolean> insertOneOrder(Order customerOrder, int status) {
    OrderReport orderReport = new OrderReport();
    orderMapper.mapOrderToOrderReport(orderReport, customerOrder);
    orderReport.setOrderCode(getOrderCode());
    orderReport.setOrderStatus(status);

    try {
      final CompletableFuture<File> googleProductFile
          = uploadFile(new java.io.File(orderReport.getSamplePictureLink()));
      Optional<java.io.File> qrFile
          = qrService.generateQRCodeImage(
          objectMapper.writeValueAsString(orderMapper.toDeliveryQr(orderReport)));
      CompletableFuture<File> googleQrFile = null;

      if (qrFile.isPresent()) {
        googleQrFile = uploadFile(qrFile.get());
      }

      setupDeliveryDateTime(orderReport);
      updateOrderDTO(customerOrder, orderReport);

      orderReport.setSamplePictureLink(
          String.format(GOOGLE_IMAGE_LINK, googleProductFile.join().getId()));

      if (qrFile.isPresent()) {
        orderReport.setQrLink(String.format(GOOGLE_IMAGE_LINK, googleQrFile.join().getId()));
      }

      orderReportRepository.save(orderReport);

    } catch (ParseException e) {
      return Optional.empty();
    } catch (Exception ex) {
      return Optional.empty();
    }
    return Optional.of(Boolean.TRUE);
  }

  private CompletableFuture<File> uploadFile(java.io.File rawFile) {

    File fileMetadata = new File();
    fileMetadata.setName(String.format("%s.jpg", UUID.randomUUID()));
    fileMetadata.setParents(Collections.singletonList(BLOEMIST_FOLDER_ID));
    fileMetadata.setMimeType(MEDIA);

    return CompletableFuture.supplyAsync(() -> {
      FileContent mediaContent = new FileContent(IMAGE_JPEG_VALUE, rawFile);

      final File googleFile;
      try {
        googleFile = googleDrive.files().create(fileMetadata, mediaContent)
            .setFields(String.join(",", ID, WEB_VIEW_LINK))
            .execute();

        moveFileToTrash(googleFile);
      } catch (Exception e) {
        return null;
      }
      return googleFile;
    });
  }

  private void stopLoadingMessage() {
    CompletableFuture.runAsync(() -> publisher.publishEvent(new MessageLoading(Boolean.FALSE)));
  }

  private void sendLoadingMessage() {
    publisher.publishEvent(new MessageLoading(Boolean.TRUE));
  }

  private void updateFieldsCanChange(Order order, OrderReport orderReport)
      throws IOException, ParseException {
    var deposit = NumberUtils.parseNumber(currencyToStringNumber(order.getDeposit()),
        BigDecimal.class);
    var remain = NumberUtils.parseNumber(currencyToStringNumber(order.getRemain()),
        BigDecimal.class);
    var total = NumberUtils.parseNumber(currencyToStringNumber(order.getTotal()), BigDecimal.class);
    var deliveryFee = NumberUtils.parseNumber(currencyToStringNumber(order.getDeliveryFee()),
        BigDecimal.class);
    var vatFee = NumberUtils.parseNumber(currencyToStringNumber(order.getVatFee()),
        BigDecimal.class);
    var actualPrice = NumberUtils.parseNumber(currencyToStringNumber(order.getActualPrice()),
        BigDecimal.class);
    var salePrice = NumberUtils.parseNumber(currencyToStringNumber(order.getSalePrice()),
        BigDecimal.class);
    var discount = NumberUtils.parseNumber(currencyToStringNumber(order.getDiscount()),
        BigDecimal.class);

    var materialFee = NumberUtils.parseNumber(currencyToStringNumber(order.getMaterialsFee()),
        BigDecimal.class);

    var actualDeliveryFee = NumberUtils.parseNumber(
        currencyToStringNumber(order.getActualDeliveryFee()),
        BigDecimal.class);

    var actualVatFee = NumberUtils.parseNumber(currencyToStringNumber(order.getActualVatFee()),
        BigDecimal.class);

    orderReport.setDepositAmount(deposit);
    orderReport.setRemainingAmount(remain);
    orderReport.setTotalAmount(total);
    orderReport.setDeliveryFee(deliveryFee);
    orderReport.setVatFee(vatFee);
    orderReport.setActualPrice(actualPrice);
    orderReport.setSalePrice(salePrice);
    orderReport.setDiscount(discount);
    // change info customer
    orderReport.setClientName(order.getCustomerName());
    orderReport.setClientPhone(order.getCustomerPhone());
    orderReport.setClientSocialLink(order.getCustomerSocialLink());
    orderReport.setClientSource(order.getCustomerSource());
    // change delivery info
    orderReport.setDeliveryAddress(order.getDeliveryAddress());
    orderReport.setReceiver(order.getReceiverName());
    orderReport.setReceiverPhone(order.getReceiverPhone());
    orderReport.setDeliveryTime(order.getDeliveryHour());
    orderReport.setDeliveryDate(Utils.toDate(order.getDeliveryDate()));
    // change order con
    orderReport.setOrderDescription(order.getOrderDescription());
    orderReport.setBannerContent(order.getBanner());
    orderReport.setRemark(order.getCustomerNote());
    orderReport.setOrderStatus(OrderState.getState(order.getStatus()));
    orderReport.setSamplePictureLink(order.getImagePath());
    // update file
    if (!orderReport.getSamplePictureLink().contains("http")) {
      final CompletableFuture<File> googleFile = uploadFile(
          new java.io.File(orderReport.getSamplePictureLink()));
      orderReport.setSamplePictureLink(String.format(GOOGLE_IMAGE_LINK, googleFile.join().getId()));
    }

    orderReport.setMaterialsFee(materialFee);
    orderReport.setActualDeliveryFee(actualDeliveryFee);
    orderReport.setActualVatFee(actualVatFee);

    setupDeliveryDateTime(orderReport);
  }

  private void moveFileToTrash(File googleFile) throws IOException {
    File trash = new File();
    trash.setTrashed(Boolean.TRUE);
    googleDrive.files().update(googleFile.getId(), trash).execute();
  }

  private void updateOrderDTO(Order customerOrder, OrderReport orderReport) {
    customerOrder.setCode(orderReport.getOrderCode());
    customerOrder.setPriority(orderReport.getOrderStatus());
    customerOrder.setImagePath(orderReport.getSamplePictureLink());
    customerOrder.setIsSelected(Boolean.FALSE);
  }

  private void setupDeliveryDateTime(OrderReport orderReport) throws ParseException {

    String[] deliveryRange = orderReport.getDeliveryTime().split("-");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    Time startTime = new Time(sdf.parse(deliveryRange[0].strip()).getTime());
    Time endTime = new Time(sdf.parse(deliveryRange[1].strip()).getTime());

    LocalDateTime startDelivery = LocalDateTime.ofInstant(orderReport.getDeliveryDate().toInstant(),
            ZoneOffset.systemDefault())
        .plusMinutes(startTime.getMinutes())
        .plusHours(startTime.getHours());

    LocalDateTime endDelivery = LocalDateTime.ofInstant(orderReport.getDeliveryDate().toInstant(),
            ZoneOffset.systemDefault())
        .plusMinutes(endTime.getMinutes())
        .plusHours(endTime.getHours());

    orderReport.setDeliveryStartRange(startDelivery);
    orderReport.setDeliveryEndRange(endDelivery);
  }
}
