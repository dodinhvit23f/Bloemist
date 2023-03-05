package com.bloemist.converters;

import com.bloemist.dto.Order;
import com.utils.Utils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.bloemist.dto.CustomerOrder;
import com.bloemist.entity.OrderReport;
import org.mapstruct.factory.Mappers;


@Mapper
public interface OrderMapper {
  OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);
  @Mapping(target = "clientName", source = "order.customerName")
  @Mapping(target = "clientPhone", source = "order.customerPhone")
  @Mapping(target = "clientSocialLink", source = "order.customerSocialLink")
  @Mapping(target = "clientSource", source = "order.customerSource")
  @Mapping(target = "deliveryAddress", source = "order.deliveryAddress")
  @Mapping(target = "receiver", source = "order.receiverName")
  @Mapping(target = "receiverPhone", source = "order.receiverPhone")
  @Mapping(target = "orderDate", source = "order.orderDate")
  @Mapping(target = "deliveryDate", source = "order.receiveDate")
  @Mapping(target = "deliveryTime", source = "order.receiveTime")
  @Mapping(target = "samplePictureLink", source = "order.imagePath")
  @Mapping(target = "orderDescription", source = "order.orderDescription")
  @Mapping(target = "remark", source = "order.orderNote")
  @Mapping(target = "bannerContent", source = "order.banner")
  @Mapping(target = "discount", source = "order.discount")
  @Mapping(target = "vatFee", source = "order.vatFee")
  @Mapping(target = "deliveryFee", source = "order.deliveryFee")
  @Mapping(target = "actualPrice", source = "order.truePrice")
  @Mapping(target = "salePrice", source = "order.salePrice")
  @Mapping(target = "depositAmount", source = "order.depositAmount")
  @Mapping(target = "remainingAmount", source = "order.remainAmount")
  @Mapping(target = "totalAmount", source = "order.totalBill")
  OrderReport customerOrderToOrder(CustomerOrder order);

  @Mapping(source = "orderReport.clientName", target = "customerName")
  @Mapping(source = "orderReport.clientPhone", target = "customerPhone")
  @Mapping(source = "orderReport.clientSocialLink", target = "customerSocialLink")
  @Mapping(source = "orderReport.clientSource", target = "customerSource")
  @Mapping(source = "orderReport.deliveryAddress", target = "deliveryAddress")
  @Mapping(source = "orderReport.receiver", target = "receiverName")
  @Mapping(source = "orderReport.receiverPhone", target = "receiverPhone")
  @Mapping(source = "orderReport.orderDate", target = "orderDate")
  @Mapping(source = "orderReport.deliveryTime", target = "receiveDate")
  @Mapping(source = "orderReport.samplePictureLink", target = "imagePath")
  @Mapping(source = "orderReport.orderDescription", target = "orderDescription")
  @Mapping(source = "orderReport.remark", target = "orderNote")
  @Mapping(source = "orderReport.bannerContent", target = "banner")
  @Mapping(source = "orderReport.discount", target = "discount")
  @Mapping(source = "orderReport.vatFee", target = "vatFee")
  @Mapping(source = "orderReport.deliveryFee", target = "deliveryFee")
  @Mapping(source = "orderReport.actualPrice", target = "truePrice")
  @Mapping(source = "orderReport.salePrice", target = "salePrice")
  @Mapping(source = "orderReport.depositAmount", target = "depositAmount")
  @Mapping(source = "orderReport.remainingAmount", target = "remainAmount")
  @Mapping(source = "orderReport.totalAmount", target = "totalBill")
  CustomerOrder orderToCustomerOrder(OrderReport orderReport);


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
        .status(orderReport.getOrderStatus())
        .code(orderReport.getOrderCode())
        .customerSource(orderReport.getClientSource())
        .actualDeliveryFee(Utils.currencyFormat(orderReport.getActualDeliveryFee().doubleValue()))
        .build();
  }
}
