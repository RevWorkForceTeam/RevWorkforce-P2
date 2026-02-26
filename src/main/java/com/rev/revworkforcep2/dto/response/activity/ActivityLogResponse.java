package com.rev.revworkforcep2.dto.response.activity;

import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private String action;
    private Long userId;
    private String userName;
    private String userRole;
    private LocalDateTime createdAt;

    public ActivityLogResponse() {}

    public ActivityLogResponse(Long id, String action,
                               Long userId, String userName, String userRole, LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserRole() { return userRole; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setAction(String action) { this.action = action; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
