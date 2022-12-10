package com.bloemist.services.impl;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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
import com.google.common.hash.Hashing;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserServiceI {

  final UserRepository userRepository;

  static final String SALT = "BLOEMIST";


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

    String hashPassword = Hashing.sha256()
        .hashString(String.join("", password, SALT), StandardCharsets.UTF_8).toString();

    if (!user.getUserName().equals(username) || !user.getPassword().equals(hashPassword)) {
      return Account.builder().role("").user(username).build();
    }

    return Account.builder()
        .role(user.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")))
        .user(username).build();
  }


  @Override
  public String createAccount(AccountDetail account) {

    Set<User> users =
        userRepository.findByUserNameOrEmail(account.getUserName(), account.getEmail());

    if (!CollectionUtils.isEmpty(users)) {

      var errors = users.stream().map(user -> {
        if (user.getUserName().equals(account.getUserName())) {
          return Constants.ERR_REGISRATOR_004;
        }
        return Constants.ERR_REGISRATOR_005;
      }).collect(Collectors.toList());

      if (errors.size() == BigInteger.TWO.intValue()) {
        return Constants.ERR_REGISRATOR_006;
      }
      return errors.get(BigInteger.ZERO.intValue());
    }


    return Constants.SUSS_LOGIN_001;
  }


}
