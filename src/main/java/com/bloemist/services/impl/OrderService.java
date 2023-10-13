package com.bloemist.services.impl;

import static com.utils.Utils.currencyToStringNumber;
import static com.utils.Utils.isDate;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

import com.bloemist.converters.OrderMapper;
import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.repositories.OrderReportRepository;
import com.bloemist.services.IOrderService;
import com.bloemist.services.ITimeService;
import com.constant.Constants;
import com.constant.OrderState;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.utils.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  ITimeService timeService;
  ApplicationEventPublisher publisher;
  OrderMapper orderMapper;
  Drive googleDrive;

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
      orders.forEach(customerOrder -> insertOneOrder(customerOrder, OrderState.getState(customerOrder.getStatus())));
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
      try{
        updateFieldsCanChange(order, optionalOrderReport.get());
      } catch (Exception exception){

      }
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_002));
      return Optional.of(Boolean.TRUE);
    }
    publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_004));
    return Optional.empty();
  }

  @Override
  public void updateOrders(List<Order> orders) {
    Optional<Order> failOrder = orders.stream()
        .filter(this::validOrder)
        .findFirst();

    Map<String, Order> orderMap = orders.stream()
        .collect(Collectors.toMap(Order::getCode, Function.identity()));

    failOrder.ifPresent(order -> {
      final List<String> codes = orders.stream().map(Order::getCode).toList();
      Map<String, OrderReport> orderReports = orderReportRepository
          .findOrderReportByOrderCodeIn(codes)
          .stream()
          .collect(Collectors.toMap(OrderReport::getOrderCode, Function.identity()));

      codes.forEach(code -> {
        if (orderReports.containsKey(code)) {
          updateFieldsCanChange(orderMap.get(code), orderReports.get(code));
        }
      });

      try {
        orderReportRepository.saveAll(orderReports.values());
        publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_002));
      } catch (Exception e){
        publisher.publishEvent(new MessageSuccess(Constants.ERR_ORDER_INFO_009));
      }
    });
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
    AtomicInteger stt = new AtomicInteger(BigInteger.ONE.intValue());

    final var startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
    final var endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

    var pageOrderReport = orderReportRepository
        .getOrdersForStaff(startDate, endDate);

    return pageOrderReport.stream()
        .map(orderReport -> {
          var order = OrderMapper.MAPPER.orderReportToOrder(orderReport);
          order.setStt(String.valueOf(stt.getAndIncrement()));
          return order;
        }).toList();
  }

  @Override
  public List<Order> getAdminPage(LocalDateTime startTime, LocalDateTime endTime) {
    AtomicInteger stt = new AtomicInteger(BigInteger.ONE.intValue());

    final var startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
    final var endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());

    var pageOrderReport = orderReportRepository
        .getOrdersAdmin(startDate, endDate);

    return pageOrderReport.stream()
        .map(orderReport -> {
          var order = OrderMapper.MAPPER.orderReportToOrder(orderReport);
          order.setStt(String.valueOf(stt.getAndIncrement()));
          return order;
        }).toList();
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
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_001));
      return Boolean.FALSE;
    }

    if (!Utils.isNumber(orderInfo.getSalePrice())
        || !Utils.isNumber(orderInfo.getDeliveryFee())
        || !Utils.isNumber(orderInfo.getVatFee())
        || !Utils.isNumber(orderInfo.getDeposit())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_002));
      return Boolean.FALSE;
    }

    if(!isDate(orderInfo.getDeliveryDate())){
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
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
      File fileMetadata = new File();
      fileMetadata.setName(String.format("%s.jpg", orderReport.getOrderCode()));
      fileMetadata.setParents(Collections.singletonList(BLOEMIST_FOLDER_ID));
      fileMetadata.setMimeType(MEDIA);

      var rawFile = new java.io.File(orderReport.getSamplePictureLink());
      FileContent mediaContent = new FileContent(IMAGE_JPEG_VALUE, rawFile);


      final File googleFile = googleDrive.files().create(fileMetadata, mediaContent)
          .setFields(String.join(",", ID, WEB_VIEW_LINK))
          .execute();

      moveFileToTrashAsync(googleFile);

      orderReport.setSamplePictureLink(String.format(GOOGLE_IMAGE_LINK, googleFile.getId()));

      orderReportRepository.save(orderReport);

      customerOrder.setCode(orderReport.getOrderCode());
      customerOrder.setPriority(orderReport.getOrderStatus());
      customerOrder.setImagePath(orderReport.getSamplePictureLink());
      customerOrder.setIsSelected(Boolean.FALSE);
    } catch (Exception ex) {
      ex.printStackTrace();
      return Optional.empty();
    }
    return Optional.of(Boolean.TRUE);
  }


  private static void updateFieldsCanChange(Order order, OrderReport orderReport) {
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
  }

  private void moveFileToTrashAsync(File googleFile) {
    CompletableFuture.runAsync(() -> {
      File trash = new File();
      trash.setTrashed(Boolean.TRUE);
      try {
        googleDrive.files().update(googleFile.getId(), trash).execute();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
