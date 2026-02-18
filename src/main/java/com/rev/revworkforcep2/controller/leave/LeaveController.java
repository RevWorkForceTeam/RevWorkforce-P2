package com.rev.revworkforcep2.controller.leave;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.request.leave.CreateHolidayRequest;
import com.rev.revworkforcep2.dto.request.leave.UpdateHolidayRequest;
import com.rev.revworkforcep2.dto.response.leave.HolidayResponse;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;
import com.rev.revworkforcep2.dto.response.leave.LeaveBalanceResponse;
import com.rev.revworkforcep2.service.leave.HolidayService;
import com.rev.revworkforcep2.service.leave.LeaveApplicationService;
import com.rev.revworkforcep2.service.leave.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveBalanceService leaveBalanceService;
    private final HolidayService holidayService;
    private final LeaveApplicationService leaveApplicationService;

    // =========================================
    // Leave Balance Endpoints
    // =========================================

    // Create initial leave balance
    @PostMapping("/balance/create")
    public ResponseEntity<LeaveBalanceResponse> createBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam int totalQuota) {
        LeaveBalanceResponse response = leaveBalanceService.createBalance(employeeId, leaveTypeId, totalQuota);
        return ResponseEntity.ok(response);
    }

    // Get specific leave balance
    @GetMapping("/balance")
    public ResponseEntity<LeaveBalanceResponse> getBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId) {
        LeaveBalanceResponse response = leaveBalanceService.getBalance(employeeId, leaveTypeId);
        return ResponseEntity.ok(response);
    }

    // Get all leave balances for an employee
    @GetMapping("/balance/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceResponse>> getEmployeeBalances(@PathVariable Long employeeId) {
        List<LeaveBalanceResponse> responses = leaveBalanceService.getEmployeeBalances(employeeId);
        return ResponseEntity.ok(responses);
    }

    // =========================================
    // Holiday Endpoints
    // =========================================

    // Create holiday
    @PostMapping("/holidays")
    public ResponseEntity<HolidayResponse> createHoliday(@RequestBody CreateHolidayRequest request) {
        HolidayResponse response = holidayService.createHoliday(request);
        return ResponseEntity.ok(response);
    }

    // Update holiday
    @PutMapping("/holidays/{id}")
    public ResponseEntity<HolidayResponse> updateHoliday(
            @PathVariable Long id,
            @RequestBody UpdateHolidayRequest request) {
        HolidayResponse response = holidayService.updateHoliday(id, request);
        return ResponseEntity.ok(response);
    }

    // Get holiday by ID
    @GetMapping("/holidays/{id}")
    public ResponseEntity<HolidayResponse> getHolidayById(@PathVariable Long id) {
        HolidayResponse response = holidayService.getHolidayById(id);
        return ResponseEntity.ok(response);
    }

    // Get all holidays
    @GetMapping("/holidays")
    public ResponseEntity<List<HolidayResponse>> getAllHolidays() {
        List<HolidayResponse> responses = holidayService.getAllHolidays();
        return ResponseEntity.ok(responses);
    }

    // Delete holiday
    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // Leave Application Endpoints
    // =========================================

    // Apply for leave
    @PostMapping("/apply")
    public ResponseEntity<LeaveApplicationResponse> applyLeave(@RequestBody ApplyLeaveRequest request) {
        LeaveApplicationResponse response = leaveApplicationService.applyLeave(request);
        return ResponseEntity.ok(response);
    }
}
