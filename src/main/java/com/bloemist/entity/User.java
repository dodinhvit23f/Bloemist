package com.bloemist.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import com.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "users",
    indexes = {@Index(columnList = "user_name , email", name = "idx_username")},
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "user_name"}, name = "uk_email"),
        @UniqueConstraint(columnNames = {"phone_number"}, name = "uk_phone")
    })
public class User extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @Column(name = "user_name", length = 30)
  private String userName;

  @Column(length = 400)
  private String password;

  @Column(name = "full_name", length = 100)
  private String fullName;

  private String email;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date")
  private Date createDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "dob")
  private Date dob;

  @Column(name = "phone_number", length = 30)
  private String phoneNumber;

  @Column(name = "gender", length = 5)
  private String gender;

  @Temporal(TemporalType.DATE)
  @Column(name = "update_date")
  private Date updateDate;

  private String address;

  @Column(name = "opt", length = Constants.OTP_LENGTH)
  private String otp;

  @Column(name = "approve_by", length = 30)
  private String approveBy;

  private BigDecimal salary;

  private boolean isDeleted;

  @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  @JoinTable(name = "staff_description", joinColumns = @JoinColumn(name = "user_id", table = "users"),
      inverseJoinColumns = @JoinColumn(name = "job_grade_id", table = "staff_description"),
      foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
  private Set<JobGrade> roles;

}
