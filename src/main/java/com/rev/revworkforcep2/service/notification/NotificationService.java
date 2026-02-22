


package com.rev.revworkforcep2.service.notification;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getAll();

    List<NotificationResponse> getMyNotifications();

    void markAsRead(Long id);

    void triggerForAllUsers(String message, String type);

    void triggerForUser(Long userId, String message, String type);
    long getUnreadCount();
}