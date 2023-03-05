package com.bloemist.services;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.bloemist.dto.Account;
import com.bloemist.dto.AccountApprovement;
import com.bloemist.dto.AccountDetail;
import com.constant.Constants;

public interface UserServiceI {

  /**
   * Check user is registered or not
   * 
   * @param username username
   * @param password Raw password
   * @return Account with role or empty string
   */
  @Transactional(readOnly = true)
  AccountDetail login(String username, String password);

  /**
   * Create user account
   * 
   * @param account detail account
   * @return MessageCode in {@link Constants}
   */
  @Transactional
  void createAccount(AccountDetail account);

  /**
   * @param account
   * @return
   */
  @Transactional(readOnly = true)
  String sendOTP(AccountDetail account);

  /**
   * @param account
   * @return
   */
  @Transactional
  String resetPassword(AccountDetail account);
  
  /**
   * 
   * @param account
   * @param newPassword
   * @return
   */
  @Transactional
  Account changeUserPassword(Account account, String newPassword);
  
  /**
   * 
   * @return
   */
  @Transactional(readOnly = true)
  List<Account> findApprovableUser();
  
  /**
   * 
   * @param approvement
   * @return
   */
  @Transactional
  void approveUserRole(AccountApprovement approvement);
  
  /**
   * 
   * @param username
   * @return
   */
  @Transactional(readOnly = true)
  AccountDetail getUserInformation(String username);
  
  /**
   * 
   * @param detail
   * @return
   */
  @Transactional
  void updateUserInformation(AccountDetail detail);
}
