package com.constant;

import com.bloemist.dto.AccountDetail;

public final class ApplicationVariable {
  private ApplicationVariable() {

  }

  private static AccountDetail user;

  public static AccountDetail getUser() {
    return user;
  }

  public static void setUser(AccountDetail loginUser) {
    user = loginUser;
  }

}
