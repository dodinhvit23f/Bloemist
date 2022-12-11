package com.bloemist.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class AccountDetail{
  
  private String username;
  private String phoneNumber;
  private String gender;
  private Date dob;
  private String password;
  private String email;
  private String address;
}
