package com.bloemist.entity;

import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@Entity
@Table(name = "product")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(name = "group_id", insertable = false, updatable = false)
  private Long groupId;

  @Column(name = "unit_id", insertable = false, updatable = false)
  private Long unitId;

  @Column(name = "fixed_price", precision = 20, scale = 0)
  private BigDecimal fixedPrice;
  
  @Column(name = "holiday_price", precision = 20, scale = 0)
  private BigDecimal holidayPrice;
  
  @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProductGroup group;
  
  @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "unit_id", nullable = false)
  private Unit unit;
}
