package com.rev.revworkforcep2.service.leave.impl;

import com.rev.revworkforcep2.dto.response.leave.LeaveBalanceResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.LeaveBalance;
import com.rev.revworkforcep2.model.LeaveType;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.LeaveBalanceRepository;
import com.rev.revworkforcep2.repository.LeaveTypeRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.leave.LeaveBalanceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;
    private final LeaveMapper leaveMapper;

    // =========================================
    // Create initial balance
    // =========================================
    @Override
    public LeaveBalanceResponse createBalance(Long employeeId, Long leaveTypeId, int totalQuota) {

        User user = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found"));

        LeaveBalance balance = new LeaveBalance();
        balance.setUser(user);
        balance.setLeaveType(leaveType);
        balance.setTotalDays(totalQuota);
        balance.setUsedDays(0);
        balance.setRemainingDays(totalQuota);

        LeaveBalance saved = leaveBalanceRepository.save(balance);

        return leaveMapper.toResponse(saved);
    }

    // =========================================
    // Get balance
    // =========================================
    @Override
    public LeaveBalanceResponse getBalance(Long employeeId, Long leaveTypeId) {

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Leave balance not found")
                );

        return leaveMapper.toResponse(balance);
    }

    // =========================================
    // Get all balances for employee
    // =========================================
    @Override
    public List<LeaveBalanceResponse> getEmployeeBalances(Long employeeId) {

        List<LeaveBalance> balances =
                leaveBalanceRepository.findByUserId(employeeId);

        if (balances.isEmpty()) {
            throw new ResourceNotFoundException("No leave balances found");
        }

        return balances.stream()
                .map(leaveMapper::toResponse)
                .toList();
    }
    @Override
    public void deductLeave(Long employeeId, Long leaveTypeId, int days) {

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Leave balance not found")
                );

        if (balance.getRemainingDays() < days) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }

        balance.setUsedDays(balance.getUsedDays() + days);
        balance.setRemainingDays(balance.getRemainingDays() - days);

        leaveBalanceRepository.save(balance);
    }
}
