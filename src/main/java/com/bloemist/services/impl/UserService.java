package com.bloemist.services.impl;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.dto.AccountDetail;
import com.bloemist.entity.Role;
import com.bloemist.entity.User;
import com.bloemist.repositories.UserRepository;
import com.bloemist.services.UserServiceI;
import com.constant.Constants;
import com.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserServiceI {

  UserRepository userRepository;

  @Override
  public Account login(String username, String password) {

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      return Account.builder().role("").build();
    }

    Optional<User> searchedUser = userRepository.findUserByUserName(username);

    if (searchedUser.isEmpty()) {
      return Account.builder().role("").user(username).build();
    }

    User user = searchedUser.get();

    String hashPassword = Utils.hashPassword(password);

    if (!user.getUserName().equals(username) || !user.getPassword().equals(hashPassword)) {
      return Account.builder().role("").user(username).build();
    }
    
    if(CollectionUtils.isEmpty(user.getRoles())) {
      return Account.builder().role("").user(username).build();
    }

    return Account.builder()
        .role(user.getRoles()
            .stream()
            .map(Role::getName)
            .collect(Collectors.joining(",")))
        .user(username)
        .password(hashPassword)
        .email(user.getEmail())
        .build();
  }

  @Override
  public String createAccount(AccountDetail account) {

    Set<User> users =
        userRepository.findByUserNameOrEmail(account.getUsername(), account.getEmail());

    if (!CollectionUtils.isEmpty(users)) {

      var errors = users.stream().map(user -> {
        if (user.getUserName().equals(account.getUsername())) {
          return Constants.ERR_REGISRATOR_004;
        }
        return Constants.ERR_REGISRATOR_005;
      }).collect(Collectors.toList());

      if (errors.size() == BigInteger.TWO.intValue()) {
        return Constants.ERR_REGISRATOR_006;
      }
      return errors.get(BigInteger.ZERO.intValue());
    }

    String hashPassword = Utils.hashPassword(account.getPassword());

    var user = User.builder().userName(account.getUsername()).password(hashPassword)
        .address(account.getAddress()).email(account.getEmail()).dob(account.getDob())
        .phoneNumber(account.getPhoneNumber()).gender(account.getGender()).build();

    userRepository.save(user);
    return Constants.SUSS_REGISRATOR_001;
  }

  @Override
  public String sendOTP(AccountDetail account) {

    Optional<User> userOptional =
        userRepository.findByUserNameAndEmail(account.getUsername(), account.getEmail());

    if (userOptional.isEmpty()) {
      return "";
    }
    
    String otp = Utils.genarateOTP(Constants.OTP_LENGTH);
    User user = userOptional.get();
    user.setOtp(otp);
    userRepository.save(user);
    
    return otp;
  }
  
  @Override
  public String resetPassword(AccountDetail account) {
    String newPassword = Utils.genarateOTP(Constants.OTP_LENGTH);
    Optional<User> userOptional = userRepository.findUserByUserName(account.getUsername());
    
    userOptional.ifPresent(user -> {
      user.setPassword(Utils.hashPassword(newPassword));
      user.setUpdateDate(Date.from(Instant.now()));
      userRepository.save(user);
    });
    
    

    return newPassword;
  }
  
  @Override
  public Account changeUserPassword(Account account, String newPassword) {
    String hashPassword = Utils.hashPassword(newPassword);
    account.setPassword(hashPassword);

    Optional<User> userOptional = userRepository.findUserByUserName(account.getUser());

    userOptional.ifPresentOrElse(user -> {
      user.setUpdateDate(Date.from(Instant.now()));
      user.setPassword(hashPassword);
      userRepository.save(user);
    }, () -> 
      System.exit(0)
    );
    return account;
  }

}
