package com.rev.revworkforcep2.service.notification;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getUserNotifications(Long userId);

    void markAsRead(Long id);
}
