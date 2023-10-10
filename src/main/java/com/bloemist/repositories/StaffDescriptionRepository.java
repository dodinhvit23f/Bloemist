package com.bloemist.repositories;

import com.bloemist.entity.StaffDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffDescriptionRepository extends JpaRepository<StaffDescription, Long> {

}
