package com.bloemist.services;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import com.bloemist.dto.Account;
import com.bloemist.entity.Role;
import com.bloemist.entity.User;
import com.bloemist.repositories.UserRepository;
import com.bloemist.services.interfaces.UserServiceI;
import com.google.common.hash.Hashing;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserServiceI {

  final UserRepository userRepository;

  static final String SALT = "bloemist";

  /**
   * 
   * @param username Username
   * @param password Raw password
   * @return User role or empty string
   */

  public Account isUser(String username, String password) {

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      return Account.builder().role("").build();
    }

    Optional<User> searchedUser = userRepository.findUserByUserName(username);

    if (searchedUser.isEmpty()) {
      return Account.builder()
          .role("")
          .user(username)
          .build();
    }

    User user = searchedUser.get();
    
    String hashPassword = Hashing.sha256()
        .hashString(String.join(password, SALT), StandardCharsets.UTF_8)
        .toString();

    if (!user.getUserName().equals(username) || !user.getPassword().equals(hashPassword)) {
      return Account.builder()
          .role("")
          .user(username).build();
    }


    return Account.builder()
        .role(user.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")))
        .user(username)
        .build();
  }


}
