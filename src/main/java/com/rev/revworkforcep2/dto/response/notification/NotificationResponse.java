package com.rev.revworkforcep2.dto.response.notification;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String message;
    private String type;
    private boolean readStatus;
    private LocalDateTime createdAt;

    public NotificationResponse(Long id, String message,
                                String type, boolean readStatus,
                                LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isReadStatus() { return readStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
