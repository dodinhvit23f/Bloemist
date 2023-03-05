package com.bloemist.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.bloemist.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUserName(String username);

  Optional<User> findByUserNameAndEmail(String userName, String email);

  Set<User> findByUserNameOrEmail(String username, String email);

  @Query("SELECT u FROM User u " +
      "LEFT JOIN StaffDescription ur ON ur.userId = u.id " +
      "WHERE u.id NOT IN ( " + "SELECT ur1.userId " +
      "FROM StaffDescription ur1 " +
      "JOIN JobGrade r ON r.id = ur1.jobGradeId " +
      "WHERE r.name = :roleName ) ")
  List<User>
  findApprovableUser(@Param("roleName") String roleName);
}
