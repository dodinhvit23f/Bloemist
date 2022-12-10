package com.bloemist.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Account {
  protected String role;
  protected String user;
}
