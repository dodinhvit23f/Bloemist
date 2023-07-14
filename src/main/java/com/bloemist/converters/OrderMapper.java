package com.bloemist.converters;

import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.constant.OrderState;
import com.utils.Utils;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.util.ObjectUtils;


@Mapper
public interface OrderMapper {

  OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);

  default OrderReport orderToOrderReport(Order order) {
    var status = OrderState.PENDING.getState();
    if (!ObjectUtils.isEmpty(order.getStatus())) {
      status = OrderState.getState(order.getStatus());
    }

    return OrderReport.builder()
        .clientName(order.getCustomerName())
        .clientPhone(order.getCustomerPhone())
        .clientSocialLink(order.getCustomerSocialLink())
        .clientSource(order.getCustomerSource())
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
            .discount(convertBigDecimal(order.getDiscount()))
        .actualPrice(convertBigDecimal(order.getActualPrice()))
        .deliveryFee(convertBigDecimal(order.getDeliveryFee()))
        .vatFee(convertBigDecimal(order.getVatFee()))
        .salePrice(convertBigDecimal(order.getSalePrice()))
        .depositAmount(convertBigDecimal(order.getDeposit()))
        .remainingAmount(convertBigDecimal(order.getRemain()))
        .totalAmount(convertBigDecimal(order.getTotal()))
        .actualDeliveryFee(convertBigDecimal(order.getActualDeliveryFee()))
        .actualVatFee(convertBigDecimal(order.getActualVatFee()))
        .materialsFee(convertBigDecimal(order.getMaterialsFee()))
        .orderStatus(status)
        .build();
  }

  private static BigDecimal convertBigDecimal(String s) {
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
