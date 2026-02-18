package com.rev.revworkforcep2.service.user;

import com.rev.revworkforcep2.dto.request.user.CreateUserRequest;
import com.rev.revworkforcep2.dto.request.user.UpdateUserRequest;
import com.rev.revworkforcep2.dto.response.user.UserResponse;

import java.util.List;

public interface UserService {

    // ----------------------------
    // Create User (Admin)
    // ----------------------------
    UserResponse createUser(CreateUserRequest request);

    // ----------------------------
    // Update User
    // ----------------------------
    UserResponse updateUser(Long id, UpdateUserRequest request);

    // ----------------------------
    // Assign Manager
    // ----------------------------
    UserResponse assignManager(Long userId, Long managerId);

    // ----------------------------
    // Get User By ID
    // ----------------------------
    UserResponse getUserById(Long id);

    // ----------------------------
    // Get All Users
    // ----------------------------
    List<UserResponse> getAllUsers();

    // ----------------------------
    // Deactivate User
    // ----------------------------
    void deactivateUser(Long id);

    // ----------------------------
    // Reactivate User
    // ----------------------------
    void reactivateUser(Long id);
}

