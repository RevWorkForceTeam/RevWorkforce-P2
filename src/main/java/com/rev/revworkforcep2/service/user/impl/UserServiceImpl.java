package com.rev.revworkforcep2.service.user.impl;

import com.rev.revworkforcep2.dto.request.user.*;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.dto.response.user.UserSummaryResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.user.UserMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.service.user.UserService;
import com.rev.revworkforcep2.specification.UserSpecification;
import com.rev.revworkforcep2.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;



    @Override
    public UserResponse getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return getUserById(userId);
    }

    @Override
    public UserResponse updateMyProfile(UpdateUserRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getEmergencyContact() != null) user.setEmergencyContact(request.getEmergencyContact());

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void changeMyPassword(String currentPassword, String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessValidationException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }



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
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        createRoleBasedLeaveBalances(savedUser);

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
    public List<UserSummaryResponse> getUsersByDepartment(Long departmentId) {
        return userRepository.findByDepartmentIdAndActiveTrue(departmentId)
                .stream()
                .map(userMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<UserSummaryResponse> getUsersByManager(Long managerId) {
        return userRepository.findByManagerIdAndActiveTrue(managerId)
                .stream()
                .map(userMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<UserSummaryResponse> filterUsers(Long departmentId,
                                                 Long designationId,
                                                 Boolean active,
                                                 String role) {

        Specification<User> spec =
                UserSpecification.filterUsers(departmentId, designationId, active, role);

        return userRepository.findAll(spec)
                .stream()
                .map(userMapper::toSummaryResponse)
                .toList();
    }


    @Override
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        );
    }
    
    private void createRoleBasedLeaveBalances(User user) {
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        
        for (LeaveType leaveType : leaveTypes) {
            boolean exists = leaveBalanceRepository
                    .findByUserIdAndLeaveTypeId(user.getId(), leaveType.getId())
                    .isPresent();
                    
            if (exists) continue;
            
            LeaveBalance balance = new LeaveBalance();
            balance.setUser(user);
            balance.setLeaveType(leaveType);
            
            int quota = getRoleBasedQuota(user.getRole(), leaveType.getName());
            balance.setTotalDays(quota);
            balance.setUsedDays(0);
            balance.setRemainingDays(quota);
            
            leaveBalanceRepository.save(balance);
        }
    }
    
    private int getRoleBasedQuota(Role role, String leaveTypeName) {
        return switch (role) {
            case ADMIN -> switch (leaveTypeName) {
                case "Sick Leave" -> 15;
                case "Casual Leave" -> 20;
                case "Annual Leave" -> 30;
                default -> 12;
            };
            case MANAGER -> switch (leaveTypeName) {
                case "Sick Leave" -> 12;
                case "Casual Leave" -> 18;
                case "Annual Leave" -> 25;
                default -> 10;
            };
            case EMPLOYEE -> switch (leaveTypeName) {
                case "Sick Leave" -> 10;
                case "Casual Leave" -> 15;
                case "Annual Leave" -> 21;
                default -> 8;
            };
        };
    }
}