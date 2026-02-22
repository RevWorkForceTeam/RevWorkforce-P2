package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_Id(Long userId);

//    List<Notification> findByUser_IdAndReadStatusFalse(Long userId);
long countByUser_IdAndReadStatusFalse(Long userId);
}
