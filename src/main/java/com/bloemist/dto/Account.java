package com.bloemist.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Account {
  private String role;
  private String user;
  private String password;
  private String email;
}
