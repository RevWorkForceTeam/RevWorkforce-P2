package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.LeaveApplication;
import com.rev.revworkforcep2.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByUserId(Long userId);

//change bby suji
    List<LeaveApplication> findByUserManagerIdAndStatus(
            Long managerId,
            LeaveStatus status);
}
