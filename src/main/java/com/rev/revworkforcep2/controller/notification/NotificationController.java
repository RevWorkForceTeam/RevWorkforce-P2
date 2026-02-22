//package com.rev.revworkforcep2.controller.notification;
//
//import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
//import com.rev.revworkforcep2.service.notification.NotificationService;
//import com.rev.revworkforcep2.util.ApiResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService service;
//
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll() {
//
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        200,
//                        "Notifications fetched successfully",
//                        service.getAll()
//                )
//        );
//    }
//
//    @GetMapping("/me")
//    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
//    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUser(
//            @PathVariable Long userId) {
//
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        200,
//                        "User notifications fetched successfully",
//                        service.getByUser(userId)
//                )
//        );
//    }
//
//    @PutMapping("/{id}/read")
//    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
//    public ResponseEntity<ApiResponse<Void>> markAsRead(
//            @PathVariable Long id) {
//
//        service.markAsRead(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        200,
//                        "Notification marked as read",
//                        null
//                )
//        );
//    }
//}





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

    // 🔹 ADMIN → View all notifications
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "All notifications fetched successfully",
                        service.getAll()
                )
        );
    }

    // 🔹 ALL USERS → View own notifications
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "My notifications fetched successfully",
                        service.getMyNotifications()
                )
        );
    }

    // 🔹 ALL USERS → Mark own notification as read
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {

        service.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Notification marked as read",
                        null
                )
        );
    }
    @GetMapping("/me/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Unread count fetched",
                        service.getUnreadCount()
                )
        );
    }
}