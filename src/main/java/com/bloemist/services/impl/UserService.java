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

  private static final String EMPTY = "";

  UserRepository userRepository;

  RoleRepository roleRepository;

  @Override
  public AccountDetail login(String username, String password) {

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      return AccountDetail.builder().role(EMPTY).build();
    }

    Optional<User> searchedUser = userRepository.findByUserName(username);

    if (searchedUser.isEmpty()) {
      return AccountDetail.builder().role(EMPTY).username(username).build();
    }

    User user = searchedUser.get();

    String hashPassword = Utils.hashPassword(password);

    if (!user.getUserName().equals(username) || !user.getPassword().equals(hashPassword)) {
      return AccountDetail.builder().role(EMPTY).username(username).build();
    }

    if (CollectionUtils.isEmpty(user.getRoles())) {
      return AccountDetail.builder().role(EMPTY).username(username).build();
    }

    return AccountDetail.builder()
        .role(user.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")))
        .username(username)
        .fullName(user.getFullName())
        .address(user.getAddress())
        .dob(user.getDob())
        .phoneNumber(user.getPhoneNumber())
        .gender(user.getGender())
        .password(user.getPassword())
        .build();
  }

  @Override
  public Account changeUserPassword(Account account, String newPassword) {
    String hashPassword = Utils.hashPassword(newPassword);
    account.setPassword(hashPassword);

    Optional<User> userOptional = userRepository.findByUserName(account.getUsername());

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

    return users.stream().map(user -> Account.builder().username(user.getUserName()).build())
        .collect(Collectors.toList());

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

    var user = User.builder()
        .userName(account.getUsername())
        .password(hashPassword)
        .address(account.getAddress())
        .email(account.getEmail())
        .dob(account.getDob())
        .phoneNumber(account.getPhoneNumber())
        .gender(account.getGender())
        .fullName(account.getFullName())
        .createDate(Date.from(Instant.now()))
        .build();

    userRepository.save(user);
    return Constants.SUSS_REGISRATOR_001;
  }

  @Override
  public String sendOTP(AccountDetail account) {

    Optional<User> userOptional =
        userRepository.findByUserNameAndEmail(account.getUsername(), account.getEmail());

    if (userOptional.isEmpty()) {
      return EMPTY;
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
  public String approveUserRole(AccountApprovement approvement) {
    if (Objects.isNull(approvement.getApprovedUser())
        || Objects.isNull(approvement.getApprover())) {
      return Constants.ERR_USER_APPROVEMENT_002;
    }

    if (!approvement.getApprover().getRole().contains(Constants.MANAGER)) {
      return Constants.ERR_USER_APPROVEMENT_001;
    }

    Optional<User> userOptional =
        userRepository.findByUserName(approvement.getApprovedUser().getUsername());

    if (userOptional.isEmpty()) {
      return Constants.ERR_USER_APPROVEMENT_003;
    }

    User approvedUser = userOptional.get();

    Optional<Role> roleOptional =
        roleRepository.findByName(approvement.getApprovedUser().getRole());

    if (roleOptional.isEmpty()) {
      return Constants.ERR_USER_APPROVEMENT_004;
    }

    if (CollectionUtils.isEmpty(approvedUser.getRoles())) {
      Set<Role> roles = new HashSet<>();
      roles.add(roleOptional.get());
      approvedUser.setRoles(roles);
    } else {
      if (!approvedUser.getRoles().add(roleOptional.get())) {
        return Constants.ERR_USER_APPROVEMENT_005;
      }
    }
    approvedUser.setApproveBy(approvement.getApprover().getUsername());

    userRepository.save(approvedUser);

    return Constants.SUSS_USER_APPROVEMENT;
  }
  
  @Override
  public AccountDetail getUserInformation(String username) {
    Optional<User> userOptional = userRepository.findByUserName(username);
    if (userOptional.isEmpty()) {
      return AccountDetail.builder().build();
    }

    var user = userOptional.get();
    
    return  AccountDetail.builder()
        .username(username)
        .fullName(user.getFullName())
        .address(user.getAddress())
        .dob(user.getDob())
        .phoneNumber(user.getPhoneNumber())
        .gender(user.getGender())
        .password(user.getPassword())
        .build();
  }
  @Override
  
  public String updateUserInformation(AccountDetail detail) {
    Optional<User> userOptional = userRepository.findByUserName(detail.getUsername());
    
    if (userOptional.isEmpty()) {
      return Constants.ERR_USER_INFO_001;
    }
    
    User user = userOptional.get();
    
    if(Objects.nonNull(detail.getDob())) {
      user.setDob(detail.getDob());
    }
    if(Objects.nonNull(detail.getAddress())) {
      user.setAddress(detail.getAddress());
    }
    if(Objects.nonNull(detail.getGender())) {
      user.setGender(detail.getGender());
    }
    if(Objects.nonNull(detail.getFullName())) {
      user.setFullName(detail.getFullName());
    }
    
    if(Objects.nonNull(detail.getPhoneNumber())) {
      user.setFullName(detail.getPhoneNumber());
    }
   
    userRepository.save(user);
    return Constants.SUSS_USER_INFO_001;
  } 
}
