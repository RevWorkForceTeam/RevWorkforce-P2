package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.response.leave.LeaveBalanceResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.LeaveBalance;
import com.rev.revworkforcep2.model.LeaveType;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.LeaveBalanceRepository;
import com.rev.revworkforcep2.repository.LeaveTypeRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.security.util.SecurityUtils;
import com.rev.revworkforcep2.service.leave.impl.LeaveBalanceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveBalanceServiceImplTest {

    @Mock private LeaveBalanceRepository leaveBalanceRepository;
    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private UserRepository userRepository;
    @Mock private LeaveMapper leaveMapper;

    @InjectMocks
    private LeaveBalanceServiceImpl service;

    // CREATE SUCCESS
    @Test
    void createBalance_shouldCreateSuccessfully() {

        User user = new User();
        LeaveType type = new LeaveType();
        LeaveBalance saved = new LeaveBalance();
        LeaveBalanceResponse response = new LeaveBalanceResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(leaveTypeRepository.findById(2L)).thenReturn(Optional.of(type));
        when(leaveBalanceRepository.save(any())).thenReturn(saved);
        when(leaveMapper.toResponse(saved)).thenReturn(response);

        LeaveBalanceResponse result =
                service.createBalance(1L, 2L, 10);

        assertThat(result).isEqualTo(response);
        verify(leaveBalanceRepository).save(any());
    }

    // CREATE USER NOT FOUND
    @Test
    void createBalance_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createBalance(1L, 2L, 10))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    // GET BALANCE SUCCESS
    @Test
    void getBalance_shouldReturnBalance() {

        LeaveBalance balance = new LeaveBalance();
        LeaveBalanceResponse response = new LeaveBalanceResponse();

        when(leaveBalanceRepository.findByUserIdAndLeaveTypeId(1L, 2L))
                .thenReturn(Optional.of(balance));
        when(leaveMapper.toResponse(balance))
                .thenReturn(response);

        LeaveBalanceResponse result =
                service.getBalance(1L, 2L);

        assertThat(result).isEqualTo(response);
    }

    // GET BALANCE NOT FOUND
    @Test
    void getBalance_shouldThrowException_whenNotFound() {

        when(leaveBalanceRepository.findByUserIdAndLeaveTypeId(1L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getBalance(1L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Leave balance not found");
    }

    // GET EMPLOYEE BALANCES SUCCESS
    @Test
    void getEmployeeBalances_shouldReturnList() {

        LeaveBalance balance = new LeaveBalance();
        LeaveBalanceResponse response = new LeaveBalanceResponse();

        when(leaveBalanceRepository.findByUserId(1L))
                .thenReturn(List.of(balance));
        when(leaveMapper.toResponse(balance))
                .thenReturn(response);

        List<LeaveBalanceResponse> result =
                service.getEmployeeBalances(1L);

        assertThat(result).hasSize(1);
    }

    // GET EMPLOYEE BALANCES EMPTY
    @Test
    void getEmployeeBalances_shouldThrowException_whenEmpty() {

        when(leaveBalanceRepository.findByUserId(1L))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                service.getEmployeeBalances(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No leave balances found");
    }

    // GET MY BALANCES SUCCESS
    @Test
    void getMyBalances_shouldReturnList() {

        User user = new User();
        user.setId(1L);

        LeaveBalance balance = new LeaveBalance();
        LeaveBalanceResponse response = new LeaveBalanceResponse();

        try (MockedStatic<SecurityUtils> mocked =
                     mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUsername)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));
            when(leaveBalanceRepository.findByUserId(1L))
                    .thenReturn(List.of(balance));
            when(leaveMapper.toResponse(balance))
                    .thenReturn(response);

            List<LeaveBalanceResponse> result =
                    service.getMyBalances();

            assertThat(result).hasSize(1);
        }
    }

    // GET MY BALANCES EMPTY
    @Test
    void getMyBalances_shouldThrowException_whenEmpty() {

        User user = new User();
        user.setId(1L);

        try (MockedStatic<SecurityUtils> mocked =
                     mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUsername)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));
            when(leaveBalanceRepository.findByUserId(1L))
                    .thenReturn(List.of());

            assertThatThrownBy(() ->
                    service.getMyBalances())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("No leave balances found");
        }
    }

    // DEDUCT SUCCESS
    @Test
    void deductLeave_shouldUpdateBalance() {

        LeaveBalance balance = new LeaveBalance();
        balance.setUsedDays(2);
        balance.setRemainingDays(8);

        when(leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(1L, 2L))
                .thenReturn(Optional.of(balance));

        service.deductLeave(1L, 2L, 3);

        assertThat(balance.getUsedDays()).isEqualTo(5);
        assertThat(balance.getRemainingDays()).isEqualTo(5);
        verify(leaveBalanceRepository).save(balance);
    }

    // DEDUCT INSUFFICIENT
    @Test
    void deductLeave_shouldThrowException_whenInsufficient() {

        LeaveBalance balance = new LeaveBalance();
        balance.setRemainingDays(2);

        when(leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(1L, 2L))
                .thenReturn(Optional.of(balance));

        assertThatThrownBy(() ->
                service.deductLeave(1L, 2L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient leave balance");
    }
}