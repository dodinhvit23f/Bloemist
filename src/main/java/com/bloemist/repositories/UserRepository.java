package com.bloemist.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bloemist.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserByUserName(String username);
}
