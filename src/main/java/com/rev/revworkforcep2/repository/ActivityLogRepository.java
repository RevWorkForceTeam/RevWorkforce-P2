package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUser_Id(Long userId);
}
