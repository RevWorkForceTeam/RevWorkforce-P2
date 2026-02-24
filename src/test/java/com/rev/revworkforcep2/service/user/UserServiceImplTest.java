package com.rev.revworkforcep2.service.user;

import com.rev.revworkforcep2.dto.request.user.*;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.dto.response.user.UserSummaryResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.user.UserMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.service.user.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private DesignationRepository designationRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl service;

    // CREATE SUCCESS
    @Test
    void createUser_shouldCreateSuccessfully() {

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@mail.com");
        request.setEmployeeId("EMP001");
        request.setDepartmentId(1L);
        request.setDesignationId(2L);

        Department department = new Department();
        Designation designation = new Designation();
        User user = new User();
        User saved = new User();
        UserResponse response = new UserResponse();

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(userRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(designationRepository.findById(2L)).thenReturn(Optional.of(designation));
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = service.createUser(request);

        assertThat(result).isEqualTo(response);
        verify(userRepository).save(user);
    }

    // CREATE EMAIL DUPLICATE
    @Test
    void createUser_shouldThrowException_whenEmailExists() {

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@mail.com");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.createUser(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Email already exists");
    }

    // ASSIGN MANAGER SELF
    @Test
    void assignManager_shouldThrowException_whenSelfAssigned() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.assignManager(1L, 1L))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("User cannot be own manager");
    }

    // ASSIGN MANAGER INACTIVE
    @Test
    void assignManager_shouldThrowException_whenManagerInactive() {

        User user = new User();
        user.setId(1L);

        User manager = new User();
        manager.setId(2L);
        manager.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(manager));

        assertThatThrownBy(() -> service.assignManager(1L, 2L))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Inactive manager cannot be assigned");
    }

    // DEACTIVATE SUCCESS
    @Test
    void deactivateUser_shouldDeactivateSuccessfully() {

        User user = new User();
        user.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.deactivateUser(1L);

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    // DEACTIVATE ALREADY INACTIVE
    @Test
    void deactivateUser_shouldThrowException_whenAlreadyInactive() {

        User user = new User();
        user.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.deactivateUser(1L))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("User already inactive");
    }

    // GET USERS BY DEPARTMENT
    @Test
    void getUsersByDepartment_shouldReturnSummaryList() {

        User user = new User();
        UserSummaryResponse summary = new UserSummaryResponse();

        when(userRepository.findByDepartmentIdAndActiveTrue(1L))
                .thenReturn(List.of(user));
        when(userMapper.toSummaryResponse(user))
                .thenReturn(summary);

        List<UserSummaryResponse> result =
                service.getUsersByDepartment(1L);

        assertThat(result).hasSize(1);
    }

    // FILTER USERS
    @Test
    void filterUsers_shouldReturnFilteredList() {

        User user = new User();
        UserSummaryResponse summary = new UserSummaryResponse();

        when(userRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(user));
        when(userMapper.toSummaryResponse(user))
                .thenReturn(summary);

        List<UserSummaryResponse> result =
                service.filterUsers(1L, 2L, true, "EMPLOYEE");

        assertThat(result).hasSize(1);
    }

    // GET USER BY ID NOT FOUND
    @Test
    void getUserById_shouldThrowException_whenNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }
}