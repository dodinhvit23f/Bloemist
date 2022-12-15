package com.constant;

import com.bloemist.dto.Account;

public final class ApplicationVariable {
  private ApplicationVariable() {

  }

  private static Account user;

  public static Account getUser() {
    return user;
  }

  public static void setUser(Account loginUser) {
    user = loginUser;
  }

}
