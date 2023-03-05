package com.bloemist.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderInfo {

  private final String customerName;
  private final String customerPhone;
  private final String customerSocialLink;
  private final String deliveryAddress;
  private final String deliveryTime;
  private final String truePrice;
  private final String deliveryFee;
  private final String vatFee;
  private final String salePrice;
  private final String depositAmount;
  private final String remainAmount;
  private final String totalAmount;
  private final String imagePath;

  public OrderInfo(String customerName, String customerPhone, String customerSocialLink,
      String deliveryAddress, String deliveryTime, String truePrice, String deliveryFee,
      String vatFee, String salePrice, String depositAmount, String remainAmount,
      String totalAmount, String imagePath) {
    this.customerName = customerName;
    this.customerPhone = customerPhone;
    this.customerSocialLink = customerSocialLink;
    this.deliveryAddress = deliveryAddress;
    this.deliveryTime = deliveryTime;
    this.truePrice = truePrice;
    this.deliveryFee = deliveryFee;
    this.vatFee = vatFee;
    this.salePrice = salePrice;
    this.depositAmount = depositAmount;
    this.remainAmount = remainAmount;
    this.totalAmount = totalAmount;
    this.imagePath = imagePath;
  }

  public String getCustomerName() {
    return customerName;
  }

  public String getCustomerPhone() {
    return customerPhone;
  }

  public String getCustomerSocialLink() {
    return customerSocialLink;
  }

  public String getDeliveryAddress() {
    return deliveryAddress;
  }

  public String getDeliveryTime() {
    return deliveryTime;
  }

  public String getTruePrice() {
    return truePrice;
  }

  public String getDeliveryFee() {
    return deliveryFee;
  }

  public String getVatFee() {
    return vatFee;
  }

  public String getSalePrice() {
    return salePrice;
  }

  public String getDepositAmount() {
    return depositAmount;
  }

  public String getRemainAmount() {
    return remainAmount;
  }

  public String getTotalAmount() {
    return totalAmount;
  }
}
