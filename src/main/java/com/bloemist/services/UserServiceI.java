package com.bloemist.services;

import org.springframework.transaction.annotation.Transactional;
import com.bloemist.dto.Account;
import com.bloemist.dto.AccountDetail;
import com.constant.Constants;

public interface UserServiceI {

  /**
   * Check user is registered or not
   * @param username username
   * @param password Raw password
   * @return Account with role or empty string
   */
  @Transactional(readOnly = true)
  Account login(String username, String password);
  
  /**
   * Create user account
   * 
   * @param account detail account
   * @return MessageCode in {@link Constants}
   */
  @Transactional(readOnly = true)
  String createAccount(AccountDetail account);
}
