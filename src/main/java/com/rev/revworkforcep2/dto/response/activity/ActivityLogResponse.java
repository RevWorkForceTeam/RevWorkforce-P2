package com.rev.revworkforcep2.dto.response.activity;

import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private String action;
    private Long userId;
    private LocalDateTime createdAt;

    public ActivityLogResponse() {}

    public ActivityLogResponse(Long id, String action,
                               Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public Long getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setAction(String action) { this.action = action; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
