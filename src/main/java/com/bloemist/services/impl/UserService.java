package com.bloemist.services.impl;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.dto.AccountApprovement;
import com.bloemist.dto.AccountDetail;
import com.bloemist.entity.Role;
import com.bloemist.entity.User;
import com.bloemist.repositories.RoleRepository;
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
  
  RoleRepository roleRepository;

  @Override
  public Account login(String username, String password) {

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      return Account.builder().role("").build();
    }

    Optional<User> searchedUser = userRepository.findByUserName(username);

    if (searchedUser.isEmpty()) {
      return Account.builder().role("").user(username).build();
    }

    User user = searchedUser.get();

    String hashPassword = Utils.hashPassword(password);

    if (!user.getUserName().equals(username) || !user.getPassword().equals(hashPassword)) {
      return Account.builder().role("").user(username).build();
    }

    if (CollectionUtils.isEmpty(user.getRoles())) {
      return Account.builder().role("").user(username).build();
    }

    return Account.builder()
        .role(user.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")))
        .user(username).password(hashPassword).email(user.getEmail()).build();
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
        .phoneNumber(account.getPhoneNumber()).gender(account.getGender())
        .createDate(Date.from(Instant.now())).build();

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
    Optional<User> userOptional = userRepository.findByUserName(account.getUsername());

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

    Optional<User> userOptional = userRepository.findByUserName(account.getUser());

    userOptional.ifPresentOrElse(user -> {
      user.setUpdateDate(Date.from(Instant.now()));
      user.setPassword(hashPassword);
      userRepository.save(user);
    }, () -> System.exit(0));

    return account;
  }

  @Override
  public List<Account> findApprovableUser() {

    List<User> users = userRepository.findApprovableUser(Constants.MANAGER);

    if (CollectionUtils.isEmpty(users)) {
      return Collections.emptyList();
    }

    return users.stream().map(user -> Account.builder().user(user.getUserName()).build())
        .collect(Collectors.toList());

  }

  @Override
  public String approveUserRole(AccountApprovement approvement) {
    if (Objects.isNull(approvement.getApprovedUser())
        || Objects.isNull(approvement.getApprover())) {
      return Constants.ERR_USER_APPROVEMENT_002;
    }

    if (!approvement.getApprover().getRole().contains(Constants.MANAGER)) {
      return Constants.ERR_USER_APPROVEMENT_001;
    }

    Optional<User> userOptional =
        userRepository.findByUserName(approvement.getApprovedUser().getUser());

    if (userOptional.isEmpty()) {
      return Constants.ERR_USER_APPROVEMENT_003;
    }
    
    User approvedUser = userOptional.get();
    
    Optional<Role> roleOptional = roleRepository.findByName(approvement.getApprovedUser().getRole());
    
    if(roleOptional.isEmpty()) {
      return Constants.ERR_USER_APPROVEMENT_004;
    }
    
    if(CollectionUtils.isEmpty(approvedUser.getRoles())) {
      Set<Role> roles = new HashSet<>();
      roles.add(roleOptional.get());
      approvedUser.setRoles(roles);
    }else{
      if(!approvedUser.getRoles().add(roleOptional.get())){
        return Constants.ERR_USER_APPROVEMENT_005;
      }
    }
    
    userRepository.save(approvedUser);

    return Constants.SUSS_USER_APPROVEMENT;
  }
}
