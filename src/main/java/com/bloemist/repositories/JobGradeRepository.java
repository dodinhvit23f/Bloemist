package com.bloemist.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bloemist.entity.JobGrade;

@Repository
public interface JobGradeRepository extends JpaRepository<JobGrade, Long> {
  Optional<JobGrade> findByName(String name);
}
