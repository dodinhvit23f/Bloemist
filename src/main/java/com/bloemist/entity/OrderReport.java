package com.bloemist.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.Interceptor;
import org.hibernate.annotations.DynamicUpdate;


/**
 * The persistent class for the "Oder_Report" database table.
 */
@Entity
@SuperBuilder
@Table(name = "order_report",
    indexes = {
        @Index(columnList = "order_status, delivery_date, delivery_time, order_date", name = "idx_delivery_time__status"),
        @Index(columnList = "order_code", name = "udx_order_code")
    })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DynamicUpdate
public class OrderReport extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @Column(name = "actual_delivery_fee")
  private BigDecimal actualDeliveryFee;

  @Column(name = "actual_price")
  private BigDecimal actualPrice;

  @Column(name = "actual_vat_fee")
  private BigDecimal actualVatFee;

  @Column(name = "banner_content", length = 1000)
  private String bannerContent;

  @Column(name = "client_name", length = 60)
  private String clientName;

  @Column(name = "client_phone", length = 20)
  private String clientPhone;

  @Column(name = "client_social_link", length = 300)
  private String clientSocialLink;

  @Column(name = "client_source", length = 60)
  private String clientSource;

  @Column(name = "delivery_address", length = 1000)
  private String deliveryAddress;

  @Column(name = "delivery_fee")
  private BigDecimal deliveryFee;

  @Column(name = "delivery_date")
  @Temporal(TemporalType.DATE)
  private Date deliveryDate;

  @Column(name = "delivery_time")
  private String deliveryTime;

  @Column(name = "deposit_amount")
  private BigDecimal depositAmount;

  private Integer discount;

  @Column(name = "materials_fee")
  private BigDecimal materialsFee;

  @Column(name = "order_code", length = 18, updatable = false)
  private String orderCode;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "order_date")
  private Date orderDate;

  @Column(name = "order_description", length = 8000)
  private String orderDescription;

  @Column(name = "order_status", length = 30)
  private Integer orderStatus;

  @Column(name = "receiver", length = 60)
  private String receiver;

  @Column(name = "receiver_phone", length = 20)
  private String receiverPhone;

  @Column(name = "remaining_amount")
  private BigDecimal remainingAmount;

  @Column(name = "remark", length = 300)
  private String remark;

  @Column(name = "sale_price")
  private BigDecimal salePrice;

  @Column(name = "sample_picture_link", length = 300)
  private String samplePictureLink;

  @Column(name = "vat_fee")
  private BigDecimal vatFee;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    OrderReport that = (OrderReport) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
