package com.rev.revworkforcep2.service.notification.impl;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
import com.rev.revworkforcep2.mapper.notification.NotificationMapper;
import com.rev.revworkforcep2.model.Notification;
import com.rev.revworkforcep2.repository.NotificationRepository;
import com.rev.revworkforcep2.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return mapper.toResponseList(repository.findByUserId(userId));
    }

    @Override
    public void markAsRead(Long id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setReadStatus(true);
        repository.save(notification);
    }
}
