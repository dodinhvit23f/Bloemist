package com.bloemist.converters;

import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.constant.OrderState;
import com.utils.Utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import org.springframework.util.ObjectUtils;


@Mapper(componentModel = "spring")
public interface OrderMapper {

  OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);

  @Mapping(source = Order.CUSTOMER_NAME, target = "clientName")
  @Mapping(source = Order.CUSTOMER_PHONE, target = "clientPhone")
  @Mapping(source = Order.CUSTOMER_SOCIAL_LINK, target = "clientSocialLink")
  @Mapping(source = Order.CUSTOMER_SOURCE, target = "clientSource")
  @Mapping(source = Order.RECEIVER_NAME, target = "receiver")
  @Mapping(source = Order.RECEIVER_PHONE, target = "receiverPhone")
  @Mapping(source = Order.ORDER_DESCRIPTION, target = "orderDescription")
  @Mapping(source = Order.CUSTOMER_NOTE, target = "remark")
  @Mapping(source = Order.BANNER, target = "bannerContent")
  @Mapping(source = Order.DELIVERY_ADDRESS, target = "deliveryAddress")
  @Mapping(target = "orderDate", expression = "java(com.utils.Utils.toDate(order.getOrderDate()))")
  @Mapping(target = "deliveryDate", expression = "java(com.utils.Utils.toDate(order.getDeliveryDate()))")
  @Mapping(source = Order.DELIVERY_HOUR, target = "deliveryTime")
  @Mapping(source = Order.IMAGE_PATH, target = "samplePictureLink")
  @Mapping(target = "discount", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getDiscount()))")
  @Mapping(target = "actualPrice", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getActualPrice()))")
  @Mapping(target = "deliveryFee", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getDeliveryFee()))")
  @Mapping(target = "vatFee", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getVatFee()))")
  @Mapping(target = "salePrice", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getSalePrice()))")
  @Mapping(target = "depositAmount", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getDeposit()))")
  @Mapping(target = "remainingAmount", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getRemain()))")
  @Mapping(target = "totalAmount", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getTotal()))")
  @Mapping(target = "actualDeliveryFee", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getActualDeliveryFee()))")
  @Mapping(target = "actualVatFee", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getActualVatFee()))")
  @Mapping(target = "materialsFee", expression = "java(com.bloemist.converters.OrderMapper.convertBigDecimal(order.getMaterialsFee()))")
  @Mapping(target = "orderStatus", ignore = true)
  void mapOrderToOrderReport(@MappingTarget OrderReport orderReport, Order order);

  static BigDecimal convertBigDecimal(String s) {
    return Objects.isNull(s) ? BigDecimal.ZERO : new BigDecimal(s);
  }

  default Order orderReportToOrder(OrderReport orderReport) {
    return Order.builder()
        .customerName(orderReport.getClientName())
        .customerPhone(orderReport.getClientPhone())
        .customerNote(orderReport.getRemark())
        .customerSocialLink(orderReport.getClientSocialLink())
        .orderDescription(orderReport.getOrderDescription())
        .orderDate(Utils.formatDate(orderReport.getOrderDate()))
        .banner(orderReport.getBannerContent())
        .deliveryAddress(orderReport.getDeliveryAddress())
        .receiverName(orderReport.getReceiver())
        .receiverPhone(orderReport.getReceiverPhone())
        .deliveryHour(String.valueOf(orderReport.getDeliveryTime()))
        .deliveryDate(Utils.formatDate(orderReport.getDeliveryDate()))
        .imagePath(orderReport.getSamplePictureLink())
        .deliveryFee(Utils.currencyFormat(orderReport.getDeliveryFee().doubleValue()))
        .vatFee(String.valueOf(orderReport.getVatFee().doubleValue()))
        .actualPrice(Utils.currencyFormat(orderReport.getActualPrice().doubleValue()))
        .discount(Utils.currencyFormat(orderReport.getDiscount().doubleValue()))
        .salePrice(Utils.currencyFormat(orderReport.getSalePrice().doubleValue()))
        .remain(Utils.currencyFormat(orderReport.getRemainingAmount().doubleValue()))
        .deposit(Utils.currencyFormat(orderReport.getDepositAmount().doubleValue()))
        .total(Utils.currencyFormat(orderReport.getTotalAmount().doubleValue()))
        .status(Arrays.stream(OrderState.values())
            .filter(orderState -> orderReport.getOrderStatus() == orderState.getState())
            .findFirst().orElseThrow().getStateText())
        .code(orderReport.getOrderCode())
        .customerSource(orderReport.getClientSource())
        .actualDeliveryFee(Utils.currencyFormat(orderReport.getActualDeliveryFee().doubleValue()))
        .actualVatFee(Utils.currencyFormat(orderReport.getActualVatFee().doubleValue()))
        .priority(orderReport.getOrderStatus())
        .isSelected(Boolean.FALSE)
        .build();
  }
}
