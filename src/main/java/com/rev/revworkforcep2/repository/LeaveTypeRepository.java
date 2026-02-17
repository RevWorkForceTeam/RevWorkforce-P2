package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByName(String name);

    boolean existsByName(String name);
}
