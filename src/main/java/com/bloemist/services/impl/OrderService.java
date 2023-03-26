package com.bloemist.services.impl;

import com.bloemist.converters.OrderMapper;
import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.repositories.OrderReportRepository;
import com.bloemist.services.IOrderService;
import com.constant.Constants;
import com.constant.OrderState;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.weaver.ast.Or;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {

  OrderReportRepository orderReportRepository;
  ApplicationEventPublisher publisher;

  @Override
  public void createNewOrder(Order customerOrder) {
    OrderReport orderReport = OrderMapper.MAPPER.orderToOrderReport(customerOrder);

    orderReport.setOrderCode(getOrderCode());

    try {
      var order = OrderMapper.MAPPER.orderReportToOrder(orderReportRepository.save(orderReport));
      order.setCode(orderReport.getOrderCode());
      order.setPriority(orderReport.getOrderStatus());
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_001));
    } catch (Exception ex) {
      publisher.publishEvent(new MessageWarning(Constants.CONNECTION_FAIL));
    }
  }

  private static String getOrderCode() {
    return String.format("%s%d", Constants.ORDER_CODER_PRE_FIX, System.nanoTime());
  }

  @Override
  public void createNewOrders(Collection<Order> orders) {

    List<OrderReport> orderReports = orders.stream()
        .map(OrderMapper.MAPPER::orderToOrderReport)
        .map(orderReport -> {
          orderReport.setOrderCode(getOrderCode());
          return orderReport;
        })
        .collect(Collectors.toList());

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
    var optionalOrderReport = orderReportRepository.findByOrderCode(order.getCode());
    optionalOrderReport.ifPresentOrElse(orderReport -> {
      // change money

      var deposit = NumberUtils.parseNumber(order.getDeposit(), BigDecimal.class);
      var remain = NumberUtils.parseNumber(order.getRemain(), BigDecimal.class);
      var total = NumberUtils.parseNumber(order.getTotal(), BigDecimal.class);
      var deliveryFee = NumberUtils.parseNumber(order.getDeliveryFee(), BigDecimal.class);
      var vatFee = NumberUtils.parseNumber(order.getVatFee(), BigDecimal.class);
      var actualPrice = NumberUtils.parseNumber(order.getActualPrice(), BigDecimal.class);
      var salePrice = NumberUtils.parseNumber(order.getSalePrice(), BigDecimal.class);
      var discount = NumberUtils.parseNumber(order.getDiscount(), Integer.class);

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

      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_002));
    }, () -> publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_004)));
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
        }).collect(Collectors.toList());
  }


}
