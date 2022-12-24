package com.bloemist.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "orders")
public class Order {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "customer_name", length =  60)
  private String customerName;
  
  @Column(name = "customer_phone", length = 10)
  private String customerPhone;
  
  @Column(name = "link_info", length = 200)
  private String linkInfo;
  
  @Column(name = "source", length = 30)
  private String source;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "order_date")
  private Date orderDate;
  
  @Column(name = "shipping_address", length = 300)
  private String shippingAddress;
  
  @Column(name = "shipping_time")
  private Integer shippingTime;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "shipping_date")
  private Date shippingDate;
  
  @Column(name = "receiver_name", length =  60)
  private String receiverName;
  
  @Column(name = "receiver_phone", length = 10)
  private String receiverPhone;
  
  @Column(name = "description", length = 1000)
  private String description;
  
  @Column(name = "note", length = 300)
  private String note;
  
  @Column(name = "banner_content", length = 1000)
  private String bannerContent;
  
  @Column(name = "shipping_fee", precision = 20)
  private BigDecimal shippingFee;
  
  @Column(name = "discount")
  private Integer discount;
  
  @Column(name = "fee_vat", precision = 20)
  private BigDecimal feeVAT;
  
  @Column(name = "deposit", precision = 20)
  private BigDecimal deposit;
  
  @Column(name = "order_group", length = 8, insertable = false, updatable = false)
  private String orderGroup;
  
}
