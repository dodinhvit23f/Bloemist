package com.bloemist.services.impl;

import com.bloemist.converters.OrderMapper;
import com.bloemist.dto.CustomerOrder;
import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.repositories.OrderReportRepository;
import com.bloemist.services.OrderServiceI;
import com.constant.Constants;
import com.constant.OrderState;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements OrderServiceI {

  OrderReportRepository orderReportRepository;
  ApplicationEventPublisher publisher;

  @Override
  public void createNewOrder(CustomerOrder customerOrder) {
    OrderReport orderReport = OrderMapper.MAPPER.customerOrderToOrder(customerOrder);

    orderReport
        .setOrderCode(String.format("%s%d", Constants.ORDER_CODER_PRE_FIX, System.nanoTime()));
    orderReport.setOrderStatus(OrderState.PENDING.getState());
    orderReport.setActualDeliveryFee(BigDecimal.ZERO);
    orderReport.setActualVatFee(BigDecimal.ZERO);
    orderReport.setMaterialsFee(BigDecimal.ZERO);

    try {
      orderReportRepository.save(orderReport);
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_001));
    } catch (Exception ex) {
      publisher.publishEvent(new MessageWarning(Constants.CONNECTION_FAIL));
    }
  }

  @Override
  public void updateOrder(CustomerOrder order) {
    var optionalOrderReport = orderReportRepository.findByOrderCode(order.getCode());
    optionalOrderReport.ifPresentOrElse(orderReport -> {
      // change money
      orderReport.setDepositAmount(order.getDepositAmount());
      orderReport.setRemainingAmount(order.getRemainAmount());
      orderReport.setTotalAmount(order.getTotalBill());
      orderReport.setDeliveryFee(order.getDeliveryFee());
      orderReport.setVatFee(order.getVatFee());
      orderReport.setActualPrice(order.getTruePrice());
      orderReport.setSalePrice(order.getSalePrice());
      orderReport.setDiscount(order.getDiscount());
      // change info customer
      orderReport.setClientName(order.getCustomerName());
      orderReport.setClientPhone(order.getCustomerPhone());
      orderReport.setClientSocialLink(order.getCustomerSocialLink());
      orderReport.setClientSource(order.getCustomerSource());
      // change delivery info
      orderReport.setDeliveryAddress(order.getDeliveryAddress());
      orderReport.setReceiver(order.getReceiverName());
      orderReport.setReceiverPhone(order.getReceiverPhone());
      orderReport.setDeliveryTime(order.getReceiveTime());
      // change order con
      orderReport.setOrderDescription(order.getOrderDescription());
      orderReport.setBannerContent(order.getBanner());
      orderReport.setRemark(order.getOrderNote());

      orderReportRepository.save(orderReport);
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
  public void changeOrderState(String orderCode, OrderState state) {
    var optionalOrderReport = orderReportRepository.findByOrderCode(orderCode);

    optionalOrderReport.ifPresentOrElse(orderReport -> {
      orderReport.setOrderStatus(state.getState());
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_ORDER_INFO_002));
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
