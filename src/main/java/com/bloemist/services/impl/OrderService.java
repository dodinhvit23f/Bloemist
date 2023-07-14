package com.bloemist.services.impl;

import static com.utils.Utils.currencyToNumber;

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
import com.utils.Utils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  OrderReportRepository orderReportRepository;
  ITimeService timeService;
  ApplicationEventPublisher publisher;

  @Override
  public void createNewOrder(Order customerOrder) {
    if (Boolean.FALSE.equals(validOrder(customerOrder))) {
      return;
    }

    OrderReport orderReport = OrderMapper.MAPPER.orderToOrderReport(customerOrder);

    orderReport.setOrderCode(getOrderCode());

    try {
      orderReportRepository.save(orderReport);
      customerOrder.setCode(orderReport.getOrderCode());
      customerOrder.setPriority(orderReport.getOrderStatus());
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_001));
    } catch (Exception ex) {
      publisher.publishEvent(new MessageWarning(Constants.CONNECTION_FAIL));
    }
  }

  @Override
  public void createNewOrders(Collection<Order> orders) {

    List<OrderReport> orderReports = orders.stream()
        .map(OrderMapper.MAPPER::orderToOrderReport)
        .map(orderReport -> {
          orderReport.setOrderCode(getOrderCode());
          return orderReport;
        })
        .toList();

    try {
      orderReportRepository.saveAll(orderReports);
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_001));
      orders.forEach(order -> {
        var orderReport = orderReports.stream()
            .filter(or -> or.getClientName().equals(order.getCustomerName()) &&
                or.getClientPhone().equals(order.getCustomerPhone()) &&
                or.getClientSource().equals(order.getCustomerSource()) &&
                or.getTotalAmount().toString().equals(order.getTotal()) &&
                or.getActualPrice().toString().equals(order.getActualPrice())
            ).findFirst().orElseThrow();
        order.setCode(orderReport.getOrderCode());
        order.setPriority(orderReport.getOrderStatus());
      });
    } catch (Exception ex) {
      publisher.publishEvent(new MessageWarning(Constants.CONNECTION_FAIL));
    }
  }

  @Override
  public void updateOrder(Order order) {
    if (Boolean.FALSE.equals(validOrder(order))) {
      return;
    }

    var optionalOrderReport = orderReportRepository.findByOrderCode(order.getCode());
    optionalOrderReport.ifPresentOrElse(orderReport -> {
      updateFieldsCanChange(order, orderReport);
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_002));
    }, () -> publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_004)));
  }

  private static void updateFieldsCanChange(Order order, OrderReport orderReport) {
    var deposit = NumberUtils.parseNumber(currencyToNumber(order.getDeposit()), BigDecimal.class);
    var remain = NumberUtils.parseNumber(currencyToNumber(order.getRemain()), BigDecimal.class);
    var total = NumberUtils.parseNumber(currencyToNumber(order.getTotal()), BigDecimal.class);
    var deliveryFee = NumberUtils.parseNumber(currencyToNumber(order.getDeliveryFee()), BigDecimal.class);
    var vatFee = NumberUtils.parseNumber(currencyToNumber(order.getVatFee()), BigDecimal.class);
    var actualPrice = NumberUtils.parseNumber(currencyToNumber(order.getActualPrice()), BigDecimal.class);
    var salePrice = NumberUtils.parseNumber(currencyToNumber(order.getSalePrice()), BigDecimal.class);
    var discount = NumberUtils.parseNumber(currencyToNumber(order.getDiscount()), BigDecimal.class);

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
    // change order con
    orderReport.setOrderDescription(order.getOrderDescription());
    orderReport.setBannerContent(order.getBanner());
    orderReport.setRemark(order.getCustomerNote());
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
        if(orderReports.containsKey(code)){
          updateFieldsCanChange(orderMap.get(code), orderReports.get(code));
        }
      });

      orderReportRepository.saveAll(orderReports.values());
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
  public void changeOrderStateInfo(Order order) {
    var optionalOrderReport = orderReportRepository.findByOrderCode(order.getCode());

    optionalOrderReport.ifPresentOrElse(orderReport -> {
      orderReport.setOrderStatus(OrderState.getState(order.getStatus()));
      orderReport.setActualDeliveryFee(new BigDecimal(order.getActualDeliveryFee()));
      orderReport.setRemark(order.getCustomerNote());

      publisher.publishEvent(new MessageSuccess(Constants.SUS_ORDER_STATUS));
    }, () -> publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_004)));
  }

  @Override
  public List<Order> getPage(Date startTime, Date endTime) {
    AtomicInteger stt = new AtomicInteger(BigInteger.ONE.intValue());

    var pageOrderReport = orderReportRepository
        .getOrders(startTime, endTime);

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

    return Boolean.TRUE;
  }


  private static String getOrderCode() {
    return String.format("%s%d", Constants.ORDER_CODER_PRE_FIX, System.nanoTime());
  }
}
