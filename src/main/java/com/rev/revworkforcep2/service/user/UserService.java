package com.rev.revworkforcep2.service.user;

import com.rev.revworkforcep2.dto.request.user.*;
import com.rev.revworkforcep2.dto.response.user.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse assignManager(Long userId, Long managerId);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    void deactivateUser(Long id);

    void reactivateUser(Long id);

    UserResponse updateUserStatus(UpdateUserStatusRequest request);

    List<UserResponse> getUsersByDepartment(Long departmentId);

    List<UserResponse> getUsersByManager(Long managerId);

    List<UserResponse> filterUsers(
            Long departmentId,
            Long designationId,
            Boolean active,
            String role
    );
}
