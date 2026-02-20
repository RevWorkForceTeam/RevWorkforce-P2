package com.rev.revworkforcep2.controller.notification;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
import com.rev.revworkforcep2.service.notification.NotificationService;
import com.rev.revworkforcep2.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Notifications fetched successfully",
                        service.getAll()
                )
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "User notifications fetched successfully",
                        service.getByUser(userId)
                )
        );
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id) {

        service.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Notification marked as read",
                        null
                )
        );
    }
}
