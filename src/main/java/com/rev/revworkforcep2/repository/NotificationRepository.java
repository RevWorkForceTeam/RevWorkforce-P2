package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndReadStatusFalse(Long userId);
}
