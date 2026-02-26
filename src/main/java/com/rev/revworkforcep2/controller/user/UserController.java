package com.rev.revworkforcep2.controller.user;

import com.rev.revworkforcep2.dto.request.user.*;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.dto.response.user.UserSummaryResponse;
import com.rev.revworkforcep2.service.user.UserService;
import com.rev.revworkforcep2.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // admin operations

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody CreateUserRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "User created",
                        userService.createUser(request))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "User updated",
                        userService.updateUser(id, request))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/assign-manager")
    public ResponseEntity<ApiResponse<UserResponse>> assignManager(
            @RequestBody AssignManagerRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Manager assigned",
                        userService.assignManager(
                                request.getUserId(),
                                request.getManagerId()
                        ))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {

        userService.deactivateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(200, "User deactivated", null)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<String>> reactivateUser(@PathVariable Long id) {

        userService.reactivateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(200, "User reactivated", null)
        );
    }

    // Employee operations

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        return ResponseEntity.ok(
                ApiResponse.success(200, "Profile fetched",
                        userService.getMyProfile())
        );
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(200, "Profile updated",
                        userService.updateMyProfile(request))
        );
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PutMapping("/me/change-password")
    public ResponseEntity<ApiResponse<String>> changeMyPassword(
            @RequestBody ChangePasswordRequest request) {
        userService.changeMyPassword(request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(
                ApiResponse.success(200, "Password changed", null)
        );
    }

    // View Operations

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "User fetched",
                        userService.getUserById(id))
        );
    }


    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getAllUsers() {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Users fetched",
                        userService.getAllUsers())
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getUsersByDepartment(
            @PathVariable Long departmentId) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Users fetched",
                        userService.getUsersByDepartment(departmentId))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getUsersByManager(
            @PathVariable Long managerId) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Users fetched",
                        userService.getUsersByManager(managerId))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> filterUsers(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long designationId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String role) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Filtered users",
                        userService.filterUsers(departmentId, designationId, active, role))
        );
    }
}