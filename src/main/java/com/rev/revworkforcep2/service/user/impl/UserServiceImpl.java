package com.rev.revworkforcep2.service.user.impl;

import com.rev.revworkforcep2.dto.request.user.*;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.user.UserMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.service.user.UserService;
import com.rev.revworkforcep2.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BusinessValidationException("Email already exists");

        if (userRepository.existsByEmployeeId(request.getEmployeeId()))
            throw new BusinessValidationException("Employee ID already exists");

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        User manager = null;

        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

            if (!manager.isActive())
                throw new BusinessValidationException("Cannot assign inactive manager");

            if (!manager.getRole().equals(Role.MANAGER))
                throw new BusinessValidationException("Assigned user is not a manager");
        }

        User user = userMapper.toEntity(request);
        user.setDepartment(department);
        user.setDesignation(designation);
        user.setManager(manager);
        user.setActive(true);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        userMapper.updateEntityFromRequest(request, user);
        user.setDepartment(department);
        user.setDesignation(designation);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse assignManager(Long userId, Long managerId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        if (user.getId().equals(manager.getId()))
            throw new BusinessValidationException("User cannot be own manager");

        if (!manager.isActive())
            throw new BusinessValidationException("Inactive manager cannot be assigned");

        if (!manager.getRole().equals(Role.MANAGER))
            throw new BusinessValidationException("User is not a manager");

        user.setManager(manager);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive())
            throw new BusinessValidationException("User already inactive");

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isActive())
            throw new BusinessValidationException("User already active");

        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public UserResponse updateUserStatus(UpdateUserStatusRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(request.isActive());

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getUsersByDepartment(Long departmentId) {
        return userRepository.findByDepartmentIdAndActiveTrue(departmentId)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByManager(Long managerId) {
        return userRepository.findByManagerIdAndActiveTrue(managerId)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> filterUsers(Long departmentId,
                                          Long designationId,
                                          Boolean active,
                                          String role) {

        Specification<User> spec =
                UserSpecification.filterUsers(departmentId, designationId, active, role);

        return userRepository.findAll(spec)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        );
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
