package com.bloemist.repositories;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bloemist.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserByUserName(String username);

  Set<User> findByUserNameOrEmail(String username, String email);
}
