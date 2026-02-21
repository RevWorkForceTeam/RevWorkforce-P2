package com.rev.revworkforcep2.dto.request.notification;

import jakarta.validation.constraints.NotNull;

public class MarkNotificationRequest {

    @NotNull(message = "Notification ID is required")
    private Long notificationId;

    public MarkNotificationRequest() {}

    public MarkNotificationRequest(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
}