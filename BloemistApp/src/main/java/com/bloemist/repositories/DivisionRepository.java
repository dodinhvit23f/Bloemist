package com.bloemist.repositories;

import com.bloemist.entity.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends JpaRepository<Department, Long> {

  Optional<Department> findByName(String name);

}
