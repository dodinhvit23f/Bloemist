package com.bloemist.entity;

import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
    indexes = {@Index(columnList = "user_name", name = "ux_username", unique = true)},
    uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "uk_email")})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_name", length = 30)
  private String userName;

  @Column(length = 400)
  private String password;

  private String email;

  @Temporal(TemporalType.DATE)
  @Column(name = "create_date")
  private Date createDate;

  @Temporal(TemporalType.DATE)
  @Column(name = "update_date")
  private Date updateDate;

  private String description;

  private boolean isDeleted;

  @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", table = "users"),
      inverseJoinColumns = @JoinColumn(name = "role_id", table = "role"))
  private Set<Role> roles;
}
