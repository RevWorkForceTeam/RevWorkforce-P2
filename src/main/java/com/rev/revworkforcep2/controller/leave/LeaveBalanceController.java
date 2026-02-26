package com.rev.revworkforcep2.controller.leave;

import com.rev.revworkforcep2.dto.response.leave.LeaveBalanceResponse;
import com.rev.revworkforcep2.service.leave.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveBalanceResponse> assignLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam int totalQuota) {
        
        LeaveBalanceResponse response = leaveBalanceService.createBalance(employeeId, leaveTypeId, totalQuota);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveBalanceResponse> adjustLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam int adjustment,
            @RequestParam String reason) {
        
        LeaveBalanceResponse response = leaveBalanceService.adjustBalance(employeeId, leaveTypeId, adjustment, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaveBalanceResponse>> getAllLeaveBalances() {
        List<LeaveBalanceResponse> balances = leaveBalanceService.getAllBalances();
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<LeaveBalanceResponse>> getEmployeeBalances(@PathVariable Long employeeId) {
        List<LeaveBalanceResponse> balances = leaveBalanceService.getEmployeeBalances(employeeId);
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/my-balances")
    public ResponseEntity<List<LeaveBalanceResponse>> getMyBalances() {
        List<LeaveBalanceResponse> balances = leaveBalanceService.getMyBalances();
        return ResponseEntity.ok(balances);
    }
}