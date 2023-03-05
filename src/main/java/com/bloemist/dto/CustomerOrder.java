package com.bloemist.dto;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

  String customerName;
  String customerPhone;
  String customerSocialLink;
  String customerSource;
  String deliveryAddress;
  String receiverName;
  String receiverPhone;
  String imagePath;
  String orderDescription;
  String orderNote;
  String banner;
  String code;
  Date orderDate;
  Date receiveDate;
  String receiveTime;
  Integer discount;
  BigDecimal truePrice;
  BigDecimal vatFee;
  BigDecimal deliveryFee;
  BigDecimal salePrice;
  BigDecimal depositAmount;
  BigDecimal remainAmount;
  BigDecimal totalBill;

}
