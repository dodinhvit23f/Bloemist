package com.bloemist.dto;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AccountDetail extends Account {
  
  private String phoneNumber;
  private String gender;
  private Date dob;
  private String address;
  private String fullName;
}
