package com.bloemist.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "product_analysis",
  indexes = {@Index(columnList = "date", name="idx_date")})
public class ProductAnalysis {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date date;
  
  @Column(name = "sale_price", precision = 20, scale = 0)
  private BigDecimal salePrice;
  
  @Column(name = "import_price", precision = 20, scale = 0)
  private BigDecimal importPrice;
  
  @Column(name = "cost", precision = 20, scale = 0)
  private BigDecimal cost;
  
  @Column(name = "product_id", insertable = false, updatable = false)
  private Long productId; 
  
  @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
  
}
