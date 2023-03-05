package com.bloemist.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  public static final String STATUS = "status";
  public static final String CODE = "code";
  public static final String DELIVERY_TIME = "deliveryTime";
  public static final String DELIVERY_DATE = "deliveryDate";
  public static final String CUSTOMER_NAME = "customerName";
  public static final String CUSTOMER_SOCIAL_LINK = "customerSocialLink";
  public static final String ORDER_DESCRIPTION = "orderDescription";
  public static final String CUSTOMER_NOTE = "customerNote";
  public static final String DELIVERY_HOUR = "deliveryHour";
  public static final String DISCOUNT = "discount";
  public static final String ACTUAL_PRICE = "actualPrice";
  public static final String SALE_PRICE = "salePrice";
  public static final String VAT_FEE = "vatFee";
  public static final String DELIVERY_FEE = "deliveryFee";
  public static final String DEPOSIT = "deposit";
  public static final String REMAIN = "remain";
  public static final String TOTAL = "total";
  public static final String IMAGE_PATH = "imagePath";
  public static final String RECEIVER_NAME = "receiverName";
  public static final String RECEIVER_PHONE = "receiverPhone";
  public static final String CUSTOMER_PHONE = "customerPhone";
  public static final String BANNER = "banner";
  public static final String STT = "stt";
  public static final String DELIVERY_ADDRESS = "deliveryAddress";
  public static final String CUSTOMER_SOURCE = "customerSource";
  public static final String IS_SELECTED = "isSelected";
  public static final String ORDER_DATE = "orderDate";


  String stt;
  String status;
  String code;
  String orderDescription;
  String banner;
  String imagePath;
  String customerNote;
  String customerName;
  String customerPhone;
  String receiverName;
  String receiverPhone;
  String customerSocialLink;
  String customerSource;
  String orderDate;
  String deliveryDate;
  String deliveryHour;
  String deliveryAddress;
  String actualPrice;
  String salePrice;
  String discount;
  String vatFee;
  String deliveryFee;
  String actualDeliveryFee;
  String deposit;
  String remain;
  String total;
  Boolean isSelected;
}
