package com.bloemist.services.impl;

import com.bloemist.dto.Account;
import com.bloemist.dto.AccountApprovement;
import com.bloemist.dto.AccountDetail;
import com.bloemist.entity.JobGrade;
import com.bloemist.entity.User;
import com.bloemist.events.MessageSuccess;
import com.bloemist.events.MessageWarning;
import com.bloemist.repositories.JobGradeRepository;
import com.bloemist.repositories.UserRepository;
import com.bloemist.services.IUserService;
import com.constant.Constants;
import com.utils.Utils;

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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {

  private static final String EMPTY = "";
  UserRepository userRepository;
  JobGradeRepository jobGradeRepository;
  ApplicationEventPublisher publisher;

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
      return
          AccountDetail.builder().role(EMPTY).username(username).build();
    }
    return AccountDetail.builder()
        .role(user.getRoles().stream().map(JobGrade::getName).collect(Collectors.joining(Constants.COMMA)))
        .username(username).fullName(user.getFullName()).address(user.getAddress())
        .dob(user.getDob()).phoneNumber(user.getPhoneNumber()).gender(user.getGender())
        .password(user.getPassword()).build();

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
  public void createAccount(AccountDetail account) {

    Set<User> users =
        userRepository.findByUserNameOrEmail(account.getUsername(), account.getEmail());

    if (!CollectionUtils.isEmpty(users)) {

      var errors = users.stream().map(user -> {
        if (user.getUserName().equals(account.getUsername())) {
          return Constants.ERR_SUBSCRIBER_004;
        }
        return Constants.ERR_SUBSCRIBER_005;
      }).collect(Collectors.toList());

      if (errors.size() == BigInteger.TWO.intValue()) {
        publisher.publishEvent(new MessageWarning(Constants.ERR_SUBSCRIBER_006));
      }

      publisher.publishEvent(new MessageWarning(errors.get(BigInteger.ZERO.intValue())));
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
    publisher.publishEvent(new MessageSuccess(Constants.SUSS_SUBSCRIBER_001));
  }

  @Override
  public String sendOTP(AccountDetail account) {

    Optional<User> userOptional =
        userRepository.findByUserNameAndEmail(account.getUsername(), account.getEmail());

    if (userOptional.isEmpty()) {
      return EMPTY;
    }

    String otp = Utils.generateOTP(Constants.OTP_LENGTH);
    User user = userOptional.get();
    user.setOtp(otp);
    userRepository.save(user);

    return otp;
  }

  @Override
  public String resetPassword(AccountDetail account) {
    String newPassword = Utils.generateOTP(Constants.OTP_LENGTH);
    Optional<User> userOptional = userRepository.findByUserName(account.getUsername());

    userOptional.ifPresent(user -> {
      user.setPassword(Utils.hashPassword(newPassword));
      user.setUpdateDate(Date.from(Instant.now()));
      userRepository.save(user);
    });

    return newPassword;
  }

  @Override
  public void approveUserRole(AccountApprovement approvement) {
    if (Objects.isNull(approvement.getApprovedUser())
        || Objects.isNull(approvement.getApprover())) {
      publisher.publishEvent(new MessageSuccess(Constants.ERR_USER_APPROVEMENT_002));
    }

    if (!approvement.getApprover().getRole().contains(Constants.MANAGER)) {
      publisher.publishEvent(new MessageSuccess(Constants.ERR_USER_APPROVEMENT_001));
    }

    Optional<User> userOptional =
        userRepository.findByUserName(approvement.getApprovedUser().getUsername());

    userOptional.ifPresentOrElse(approvedUser -> {
      Optional<JobGrade> roleOptional =
          jobGradeRepository.findByName(approvement.getApprovedUser().getRole());

      roleOptional.ifPresentOrElse(role -> {
            if (CollectionUtils.isEmpty(approvedUser.getRoles())) {
              Set<JobGrade> roles = new HashSet<>();
              roles.add(role);
              approvedUser.setRoles(roles);
              approvedUser.setApproveBy(approvement.getApprover().getUsername());
            } else if (!approvedUser.getRoles().add(role)) { // can't add existed role
              publisher.publishEvent(new MessageSuccess(Constants.ERR_USER_APPROVEMENT_005));
              return;
            }
            userRepository.save(approvedUser);
            publisher.publishEvent(new MessageSuccess(Constants.SUSS_USER_APPROVEMENT));
          },
          () -> publisher.publishEvent(new MessageSuccess(Constants.ERR_USER_APPROVEMENT_004)));

    }, () -> publisher.publishEvent(new MessageSuccess(Constants.ERR_USER_APPROVEMENT_003)));
  }

  @Override
  public AccountDetail getUserInformation(String username) {
    Optional<User> userOptional = userRepository.findByUserName(username);
    if (userOptional.isEmpty()) {
      return AccountDetail.builder().build();
    }

    var user = userOptional.get();

    return AccountDetail.builder()
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
  public void updateUserInformation(AccountDetail detail) {
    Optional<User> userOptional = userRepository.findByUserName(detail.getUsername());

    userOptional.ifPresentOrElse(user -> {
      if (Objects.nonNull(detail.getDob())) {
        user.setDob(detail.getDob());
      }
      if (Objects.nonNull(detail.getAddress())) {
        user.setAddress(detail.getAddress());
      }
      if (Objects.nonNull(detail.getGender())) {
        user.setGender(detail.getGender());
      }
      if (Objects.nonNull(detail.getFullName())) {
        user.setFullName(detail.getFullName());
      }

      if (Objects.nonNull(detail.getPhoneNumber())) {
        user.setFullName(detail.getPhoneNumber());
      }

      userRepository.save(user);
      publisher.publishEvent(new MessageSuccess(Constants.SUSS_USER_INFO_001));
    }, () -> publisher.publishEvent(new MessageSuccess(Constants.ERR_USER_INFO_001)));
  }
}
