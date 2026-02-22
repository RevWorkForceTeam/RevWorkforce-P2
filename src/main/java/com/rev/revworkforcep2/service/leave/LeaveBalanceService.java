package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.response.leave.LeaveBalanceResponse;

import java.util.List;

public interface LeaveBalanceService {

    LeaveBalanceResponse createBalance(Long employeeId,
                                       Long leaveTypeId,
                                       int totalQuota);

    LeaveBalanceResponse getBalance(Long employeeId,
                                    Long leaveTypeId);

    List<LeaveBalanceResponse> getEmployeeBalances(Long employeeId);

    List<LeaveBalanceResponse> getMyBalances();   // 🔥 For logged-in user

    void deductLeave(Long employeeId,
                     Long leaveTypeId,
                     int days);
}