package com.bloemist.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class DeliveryQr {
  @JsonProperty("Người nhận")
  private String receiver;
  @JsonProperty("SĐT người nhận")
  private String receiverPhone;
  @JsonProperty("Địa chỉ")
  private String deliveryAddress;
  @JsonProperty("Ngày giao")
  @JsonFormat(shape = Shape.STRING, pattern = "dd/MM/YYYY HH:mm")
  private LocalDateTime deliveryStartRange;
  @JsonProperty("Tổng tiền")
  private String totalAmount;
}
