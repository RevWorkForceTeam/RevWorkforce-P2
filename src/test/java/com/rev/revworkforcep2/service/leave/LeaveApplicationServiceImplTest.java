package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ConflictException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.security.util.SecurityUtils;
import com.rev.revworkforcep2.service.leave.impl.LeaveApplicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveApplicationServiceImplTest {

    @Mock private HolidayRepository holidayRepository;
    @Mock private LeaveApplicationRepository leaveApplicationRepository;
    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private LeaveBalanceRepository leaveBalanceRepository;
    @Mock private UserRepository userRepository;
    @Mock private LeaveMapper leaveMapper;

    @InjectMocks
    private LeaveApplicationServiceImpl service;

    // APPLY SUCCESS
    @Test
    void applyLeave_shouldApplySuccessfully() {

        ApplyLeaveRequest request = new ApplyLeaveRequest();
        request.setFromDate(LocalDate.now());
        request.setToDate(LocalDate.now());
        request.setLeaveTypeId(1L);

        User user = new User();
        user.setId(1L);
        user.setRole(Role.EMPLOYEE);

        LeaveType type = new LeaveType();
        type.setId(1L);

        LeaveBalance balance = new LeaveBalance();
        balance.setRemainingDays(5);

        LeaveApplication entity = new LeaveApplication();
        LeaveApplication saved = new LeaveApplication();
        LeaveApplicationResponse response = new LeaveApplicationResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUsername)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));
            when(leaveTypeRepository.findById(1L))
                    .thenReturn(Optional.of(type));
            when(leaveBalanceRepository.findByUserIdAndLeaveTypeId(1L, 1L))
                    .thenReturn(Optional.of(balance));
            when(leaveMapper.toEntity(request, user, type))
                    .thenReturn(entity);
            when(leaveApplicationRepository.save(entity))
                    .thenReturn(saved);
            when(leaveMapper.toResponse(saved))
                    .thenReturn(response);

            LeaveApplicationResponse result = service.applyLeave(request);

            assertThat(result).isEqualTo(response);
        }
    }

    // APPLY INSUFFICIENT BALANCE
    @Test
    void applyLeave_shouldThrowException_whenInsufficientBalance() {

        ApplyLeaveRequest request = new ApplyLeaveRequest();
        request.setFromDate(LocalDate.now());
        request.setToDate(LocalDate.now().plusDays(3));
        request.setLeaveTypeId(1L);

        User user = new User();
        user.setId(1L);

        LeaveType type = new LeaveType();
        type.setId(1L);

        LeaveBalance balance = new LeaveBalance();
        balance.setRemainingDays(1);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUsername)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));
            when(leaveTypeRepository.findById(1L))
                    .thenReturn(Optional.of(type));
            when(leaveBalanceRepository.findByUserIdAndLeaveTypeId(1L, 1L))
                    .thenReturn(Optional.of(balance));

            assertThatThrownBy(() -> service.applyLeave(request))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessage("Insufficient leave balance");
        }
    }

    // APPROVE NOT PENDING
    @Test
    void approveLeave_shouldThrowException_whenNotPending() {

        LeaveApplication leave = new LeaveApplication();
        leave.setStatus(LeaveStatus.APPROVED);

        when(leaveApplicationRepository.findById(1L))
                .thenReturn(Optional.of(leave));

        assertThatThrownBy(() -> service.approveLeave(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Only pending leave can be approved");
    }

    // CANCEL NOT OWNER
    @Test
    void cancelLeave_shouldThrowException_whenNotOwner() {

        User owner = new User();
        owner.setId(1L);

        LeaveApplication leave = new LeaveApplication();
        leave.setStatus(LeaveStatus.PENDING);
        leave.setUser(owner);

        User current = new User();
        current.setId(2L);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUsername)
                    .thenReturn("user@mail.com");

            when(leaveApplicationRepository.findById(1L))
                    .thenReturn(Optional.of(leave));
            when(userRepository.findByEmail("user@mail.com"))
                    .thenReturn(Optional.of(current));

            assertThatThrownBy(() -> service.cancelLeave(1L))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("You can only cancel your own leave");
        }
    }

    // GET MY LEAVES
    @Test
    void getMyLeaves_shouldReturnList() {

        User user = new User();
        user.setId(1L);

        LeaveApplication leave = new LeaveApplication();
        LeaveApplicationResponse response = new LeaveApplicationResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUsername)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));
            when(leaveApplicationRepository.findByUserId(1L))
                    .thenReturn(List.of(leave));
            when(leaveMapper.toResponse(leave))
                    .thenReturn(response);

            List<LeaveApplicationResponse> result = service.getMyLeaves();

            assertThat(result).hasSize(1);
        }
    }
}