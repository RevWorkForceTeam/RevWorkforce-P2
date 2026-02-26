package com.rev.revworkforcep2.service.user;

import com.rev.revworkforcep2.dto.request.user.*;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.dto.response.user.UserSummaryResponse;

import java.util.List;

public interface UserService {



    UserResponse getMyProfile();

    UserResponse updateMyProfile(UpdateUserRequest request);

    void changeMyPassword(String currentPassword, String newPassword);


    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse assignManager(Long userId, Long managerId);



    UserResponse getUserById(Long id);



    List<UserSummaryResponse> getAllUsers();

    List<UserSummaryResponse> getUsersByDepartment(Long departmentId);

    List<UserSummaryResponse> getUsersByManager(Long managerId);

    List<UserSummaryResponse> filterUsers(
            Long departmentId,
            Long designationId,
            Boolean active,
            String role
    );



    void deactivateUser(Long id);

    void reactivateUser(Long id);

    UserResponse updateUserStatus(UpdateUserStatusRequest request);
}