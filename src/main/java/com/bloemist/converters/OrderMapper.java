package com.bloemist.converters;

import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.constant.OrderState;

import com.utils.Utils;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import org.springframework.util.ObjectUtils;

import static com.bloemist.dto.Order.STATUS_PROPERTY;


@Mapper(componentModel = "spring", imports = {ObjectUtils.class, Utils.class, OrderMapper.class, OrderState.class})
public interface OrderMapper {

  OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);

  @Mapping(source = Order.CUSTOMER_NAME, target = "clientName")
  @Mapping(source = Order.CUSTOMER_PHONE, target = "clientPhone")
  @Mapping(source = Order.CUSTOMER_SOCIAL_LINK, target = "clientSocialLink")
  @Mapping(source = Order.CUSTOMER_SOURCE, target = "clientSource")
  @Mapping(target = "receiver", expression = "java(ObjectUtils.isEmpty(order.getReceiverName()) ? order.getCustomerName() : order.getReceiverName())")
  @Mapping(target = "receiverPhone", expression = "java(ObjectUtils.isEmpty(order.getReceiverPhone()) ? order.getCustomerPhone() : order.getReceiverPhone())")
  @Mapping(source = Order.ORDER_DESCRIPTION, target = "orderDescription")
  @Mapping(source = Order.CUSTOMER_NOTE, target = "remark")
  @Mapping(source = Order.BANNER, target = "bannerContent")
  @Mapping(source = Order.DELIVERY_ADDRESS, target = "deliveryAddress")
  @Mapping(target = "orderDate", expression = "java(Utils.toDate(order.getOrderDate()))")
  @Mapping(target = "deliveryDate", expression = "java(Utils.toDate(order.getDeliveryDate()))")
  @Mapping(source = Order.DELIVERY_HOUR, target = "deliveryTime")
  @Mapping(source = Order.IMAGE_PATH, target = "samplePictureLink")
  @Mapping(target = "discount", expression = "java(OrderMapper.convertBigDecimal(order.getDiscount()))")
  @Mapping(target = "actualPrice", expression = "java(OrderMapper.convertBigDecimal(order.getActualPrice()))")
  @Mapping(target = "deliveryFee", expression = "java(OrderMapper.convertBigDecimal(order.getDeliveryFee()))")
  @Mapping(target = "vatFee", expression = "java(OrderMapper.convertBigDecimal(order.getVatFee()))")
  @Mapping(target = "salePrice", expression = "java(OrderMapper.convertBigDecimal(order.getSalePrice()))")
  @Mapping(target = "depositAmount", expression = "java(OrderMapper.convertBigDecimal(order.getDeposit()))")
  @Mapping(target = "remainingAmount", expression = "java(OrderMapper.convertBigDecimal(order.getRemain()))")
  @Mapping(target = "totalAmount", expression = "java(OrderMapper.convertBigDecimal(order.getTotal()))")
  @Mapping(target = "actualDeliveryFee", expression = "java(OrderMapper.convertBigDecimal(order.getActualDeliveryFee()))")
  @Mapping(target = "actualVatFee", expression = "java(OrderMapper.convertBigDecimal(order.getActualVatFee()))")
  @Mapping(target = "materialsFee", expression = "java(OrderMapper.convertBigDecimal(order.getMaterialsFee()))")
  @Mapping(target = "orderStatus", expression = "java(OrderState.getState(order.getStatus()))")
  void mapOrderToOrderReport(@MappingTarget OrderReport orderReport, Order order);

  static BigDecimal convertBigDecimal(String s) {
    return Objects.isNull(s) ? BigDecimal.ZERO : new BigDecimal(s);
  }

  default Order orderReportToOrder(OrderReport orderReport) {
    final String status = Arrays.stream(OrderState.values())
        .filter(orderState -> orderReport.getOrderStatus() == orderState.getState())
        .findFirst().orElseThrow().getStateText();
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
        .status(status)
        .materialsFee(Utils.currencyFormat(orderReport.getMaterialsFee().doubleValue()))
        .code(orderReport.getOrderCode())
        .customerSource(orderReport.getClientSource())
        .actualDeliveryFee(Utils.currencyFormat(orderReport.getActualDeliveryFee().doubleValue()))
        .actualVatFee(Utils.currencyFormat(orderReport.getActualVatFee().doubleValue()))
        .priority(orderReport.getOrderStatus())
        .statusProperty(new SimpleStringProperty(status))
        .customerSourceProperty(new SimpleStringProperty(orderReport.getClientSource()))
        .isSelected(Boolean.FALSE)
        .build();
  }
}
