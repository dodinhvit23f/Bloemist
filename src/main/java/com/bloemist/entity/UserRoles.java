package com.bloemist.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_roles")
public class UserRoles implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name = "user_id")
  private Long  userId;
  @Id
  @Column(name = "role_id")
  private Long roleId;
}
