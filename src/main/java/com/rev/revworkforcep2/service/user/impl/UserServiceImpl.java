package com.rev.revworkforcep2.service.user.impl;

import com.rev.revworkforcep2.dto.request.user.CreateUserRequest;
import com.rev.revworkforcep2.dto.request.user.UpdateUserRequest;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.user.UserMapper;
import com.rev.revworkforcep2.model.Department;
import com.rev.revworkforcep2.model.Designation;
import com.rev.revworkforcep2.model.Role;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.DepartmentRepository;
import com.rev.revworkforcep2.repository.DesignationRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.user.UserService;
import lombok.RequiredArgsConstructor;
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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessValidationException("Email already exists");
        }

        if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BusinessValidationException("Employee ID already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        User manager = null;
        if (request.getManagerId() != null) {

            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

            if (!manager.isActive()) {
                throw new BusinessValidationException("Cannot assign inactive manager");
            }

            if (!manager.getRole().equals(Role.MANAGER)) {
                throw new BusinessValidationException("Assigned user is not a manager");
            }
        }

        User user = userMapper.toEntity(request);

        user.setDepartment(department);
        user.setDesignation(designation);
        user.setManager(manager);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
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

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse assignManager(Long userId, Long managerId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        if (user.getId().equals(manager.getId())) {
            throw new BusinessValidationException("User cannot be assigned as their own manager");
        }

        if (!manager.isActive()) {
            throw new BusinessValidationException("Cannot assign inactive manager");
        }

        if (!manager.getRole().equals(Role.MANAGER)) {
            throw new BusinessValidationException("Assigned user is not a manager");
        }

        user.setManager(manager);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public void deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BusinessValidationException("User is already inactive");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void reactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isActive()) {
            throw new BusinessValidationException("User is already active");
        }

        user.setActive(true);
        userRepository.save(user);
    }
}

