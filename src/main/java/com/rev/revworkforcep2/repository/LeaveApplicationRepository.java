package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.LeaveApplication;
import com.rev.revworkforcep2.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByUserId(Long userId);

    List<LeaveApplication> findByStatus(LeaveStatus status);

    List<LeaveApplication> findByUserManagerId(Long managerId);
}
