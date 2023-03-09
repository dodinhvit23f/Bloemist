package com.bloemist.converters;

import com.bloemist.dto.Order;
import com.constant.OrderState;
import com.utils.Utils;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.bloemist.entity.OrderReport;
import org.mapstruct.factory.Mappers;
import org.springframework.util.NumberUtils;


@Mapper
public interface OrderMapper {
  OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);

  default OrderReport orderToOrderReport(Order order){

    return OrderReport.builder()
        .clientName(order.getCustomerName())
        .clientPhone(order.getCustomerPhone())
        .clientSocialLink(order.getCustomerSocialLink())
        .clientSource(order.getCustomerSocialLink())
        .receiver(order.getReceiverName())
        .receiverPhone(order.getCustomerPhone())
        .orderDescription(order.getOrderDescription())
        .remark(order.getCustomerNote())
        .bannerContent(order.getBanner())
        .deliveryAddress(order.getDeliveryAddress())
        .orderDate(Utils.toDate(order.getOrderDate()))
        .deliveryDate(Utils.toDate(order.getDeliveryDate()))
        .deliveryTime(order.getDeliveryHour())
        .samplePictureLink(order.getImagePath())
        .discount(Integer.parseInt(order.getDiscount()))
        .actualPrice(new BigDecimal(order.getActualPrice()))
        .deliveryFee(new BigDecimal(order.getDeliveryFee()))
        .vatFee(new BigDecimal(order.getVatFee()))
        .salePrice(new BigDecimal(order.getSalePrice()))
        .depositAmount(new BigDecimal(order.getDeposit()))
        .remainingAmount(new BigDecimal(order.getRemain()))
        .totalAmount(new BigDecimal(order.getTotal()))
        .build();
  };



  default Order orderReportToOrder(OrderReport orderReport){
    return  Order.builder()
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
        .discount(String.valueOf(orderReport.getDiscount()))
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
        .build();
  }
}
