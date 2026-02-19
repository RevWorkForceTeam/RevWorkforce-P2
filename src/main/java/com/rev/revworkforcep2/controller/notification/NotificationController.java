package com.rev.revworkforcep2.controller.notification;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
import com.rev.revworkforcep2.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/user/{userId}")
    public List<NotificationResponse> getUserNotifications(@PathVariable Long userId) {
        return service.getUserNotifications(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
    }
}
