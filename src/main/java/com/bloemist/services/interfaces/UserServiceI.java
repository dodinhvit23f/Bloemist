package com.bloemist.services.interfaces;

import org.springframework.transaction.annotation.Transactional;
import com.bloemist.dto.Account;

public interface UserServiceI {

  /**
   * 
   * @param username Username
   * @param password Raw password
   * @return User role or empty string
   */
  @Transactional(readOnly = true)
  Account login(String username, String password);

}
