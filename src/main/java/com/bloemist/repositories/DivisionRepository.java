package com.bloemist.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bloemist.entity.Department;

@Repository
public interface DivisionRepository extends JpaRepository<Department, Long> {

  Optional<Department> findByName(String name);

}
