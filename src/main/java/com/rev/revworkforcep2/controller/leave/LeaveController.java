//package com.rev.revworkforcep2.controller.leave;
//
//import com.rev.revworkforcep2.dto.request.leave.*;
//import com.rev.revworkforcep2.dto.response.leave.*;
//import com.rev.revworkforcep2.service.leave.*;
//import com.rev.revworkforcep2.util.ApiResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/leaves")
//@RequiredArgsConstructor
//public class LeaveController {
//
//    private final LeaveBalanceService leaveBalanceService;
//    private final HolidayService holidayService;
//    private final LeaveApplicationService leaveApplicationService;
//    private final LeaveTypeService leaveTypeService;
//
//    // =========================================================
//    // Leave Type Endpoints (ADMIN)
//    // =========================================================
//
//    @PostMapping("/types")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<LeaveTypeResponse>> createLeaveType(
//            @RequestBody CreateLeaveTypeRequest request) {
//
//        LeaveTypeResponse response = leaveTypeService.createLeaveType(request);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave type created successfully", response)
//        );
//    }
//
//    @PutMapping("/types/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<LeaveTypeResponse>> updateLeaveType(
//            @PathVariable Long id,
//            @RequestBody UpdateLeaveTypeRequest request) {
//
//        LeaveTypeResponse response = leaveTypeService.updateLeaveType(id, request);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave type updated successfully", response)
//        );
//    }
//
//    @GetMapping("/types/{id}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
//    public ResponseEntity<ApiResponse<LeaveTypeResponse>> getLeaveTypeById(
//            @PathVariable Long id) {
//
//        LeaveTypeResponse response = leaveTypeService.getLeaveTypeById(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave type fetched successfully", response)
//        );
//    }
//
//    @GetMapping("/types")
//    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
//    public ResponseEntity<ApiResponse<List<LeaveTypeResponse>>> getAllLeaveTypes() {
//
//        List<LeaveTypeResponse> responses = leaveTypeService.getAllLeaveTypes();
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave types fetched successfully", responses)
//        );
//    }
//
//    @DeleteMapping("/types/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<Void>> deleteLeaveType(@PathVariable Long id) {
//
//        leaveTypeService.deleteLeaveType(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave type deleted successfully", null)
//        );
//    }
//
//    // =========================================================
//    // Leave Balance Endpoints
//    // =========================================================
//
//    @PostMapping("/balance")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<LeaveBalanceResponse>> createBalance(
//            @RequestParam Long employeeId,
//            @RequestParam Long leaveTypeId,
//            @RequestParam int totalQuota) {
//
//        LeaveBalanceResponse response =
//                leaveBalanceService.createBalance(employeeId, leaveTypeId, totalQuota);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave balance created successfully", response)
//        );
//    }
//
//    @GetMapping("/balance")
//    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
//    public ResponseEntity<ApiResponse<LeaveBalanceResponse>> getBalance(
//            @RequestParam Long employeeId,
//            @RequestParam Long leaveTypeId) {
//
//        LeaveBalanceResponse response =
//                leaveBalanceService.getBalance(employeeId, leaveTypeId);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave balance fetched successfully", response)
//        );
//    }
//
//    @GetMapping("/balance/employee/{employeeId}")
//    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
//    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getEmployeeBalances(
//            @PathVariable Long employeeId) {
//
//        List<LeaveBalanceResponse> responses =
//                leaveBalanceService.getEmployeeBalances(employeeId);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Employee leave balances fetched successfully", responses)
//        );
//    }
//
//    // =========================================================
//    // Holiday Endpoints
//    // =========================================================
//
//    @PostMapping("/holidays")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<HolidayResponse>> createHoliday(
//            @RequestBody CreateHolidayRequest request) {
//
//        HolidayResponse response = holidayService.createHoliday(request);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Holiday created successfully", response)
//        );
//    }
//
//    @PutMapping("/holidays/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<HolidayResponse>> updateHoliday(
//            @PathVariable Long id,
//            @RequestBody UpdateHolidayRequest request) {
//
//        HolidayResponse response = holidayService.updateHoliday(id, request);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Holiday updated successfully", response)
//        );
//    }
//
//    @GetMapping("/holidays/{id}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
//    public ResponseEntity<ApiResponse<HolidayResponse>> getHolidayById(
//            @PathVariable Long id) {
//
//        HolidayResponse response = holidayService.getHolidayById(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Holiday fetched successfully", response)
//        );
//    }
//
//    @GetMapping("/holidays")
//    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
//    public ResponseEntity<ApiResponse<List<HolidayResponse>>> getAllHolidays() {
//
//        List<HolidayResponse> responses = holidayService.getAllHolidays();
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Holidays fetched successfully", responses)
//        );
//    }
//
//    @DeleteMapping("/holidays/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<Void>> deleteHoliday(@PathVariable Long id) {
//
//        holidayService.deleteHoliday(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Holiday deleted successfully", null)
//        );
//    }
//
//    // =========================================================
//    // Leave Application Endpoints
//    // =========================================================
//
//    @PostMapping
//    @PreAuthorize("hasRole('EMPLOYEE')")
//    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> applyLeave(
//            @RequestBody ApplyLeaveRequest request) {
//
//        LeaveApplicationResponse response =
//                leaveApplicationService.applyLeave(request);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave applied successfully", response)
//        );
//    }
//
//    @PostMapping("/{id}/approve")
//    @PreAuthorize("hasRole('MANAGER')")
//    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> approveLeave(
//            @PathVariable Long id) {
//
//        LeaveApplicationResponse response =
//                leaveApplicationService.approveLeave(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave approved successfully", response)
//        );
//    }
//
//    @PostMapping("/{id}/reject")
//    @PreAuthorize("hasRole('MANAGER')")
//    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> rejectLeave(
//            @PathVariable Long id,
//            @RequestParam String comment) {
//
//        LeaveApplicationResponse response =
//                leaveApplicationService.rejectLeave(id, comment);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave rejected successfully", response)
//        );
//    }
//
//    @PostMapping("/{id}/cancel")
//    @PreAuthorize("hasRole('EMPLOYEE')")
//    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> cancelLeave(
//            @PathVariable Long id) {
//
//        LeaveApplicationResponse response =
//                leaveApplicationService.cancelLeave(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(200, "Leave cancelled successfully", response)
//        );
//    }
//}


package com.rev.revworkforcep2.controller.leave;

import com.rev.revworkforcep2.dto.request.leave.*;
import com.rev.revworkforcep2.dto.response.leave.*;
import com.rev.revworkforcep2.service.leave.*;
import com.rev.revworkforcep2.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveBalanceService leaveBalanceService;
    private final HolidayService holidayService;
    private final LeaveApplicationService leaveApplicationService;
    private final LeaveTypeService leaveTypeService;

    // =========================================================
    // 🔹 LEAVE TYPE (ADMIN)
    // =========================================================

    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> createLeaveType(
            @Valid @RequestBody CreateLeaveTypeRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave type created successfully",
                        leaveTypeService.createLeaveType(request))
        );
    }

    @PutMapping("/types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeaveTypeRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave type updated successfully",
                        leaveTypeService.updateLeaveType(id, request))
        );
    }

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveTypeResponse>>> getAllLeaveTypes() {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave types fetched successfully",
                        leaveTypeService.getAllLeaveTypes())
        );
    }

    @DeleteMapping("/types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLeaveType(@PathVariable Long id) {

        leaveTypeService.deleteLeaveType(id);

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave type deleted successfully",
                        null)
        );
    }

    // =========================================================
    // 🔹 LEAVE BALANCE
    // =========================================================

    @GetMapping("/balance/me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getMyBalances() {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "My leave balances fetched successfully",
                        leaveBalanceService.getMyBalances())
        );
    }

    @GetMapping("/balance/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getEmployeeBalances(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Employee leave balances fetched successfully",
                        leaveBalanceService.getEmployeeBalances(employeeId))
        );
    }

    // =========================================================
    // 🔹 HOLIDAYS
    // =========================================================

    @PostMapping("/holidays")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HolidayResponse>> createHoliday(
            @Valid @RequestBody CreateHolidayRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Holiday created successfully",
                        holidayService.createHoliday(request))
        );
    }

    @GetMapping("/holidays")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<HolidayResponse>>> getAllHolidays() {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Holidays fetched successfully",
                        holidayService.getAllHolidays())
        );
    }

    // =========================================================
    // 🔹 LEAVE APPLICATION
    // =========================================================

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> applyLeave(
            @Valid @RequestBody ApplyLeaveRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave applied successfully",
                        leaveApplicationService.applyLeave(request))
        );
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> approveLeave(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave approved successfully",
                        leaveApplicationService.approveLeave(id))
        );
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> rejectLeave(
            @PathVariable Long id,
            @RequestParam String comment) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave rejected successfully",
                        leaveApplicationService.rejectLeave(id, comment))
        );
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> cancelLeave(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Leave cancelled successfully",
                        leaveApplicationService.cancelLeave(id))
        );
    }

    // =========================================================
    // 🔹 VIEW LEAVES
    // =========================================================

    @GetMapping("/me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getMyLeaves() {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "My leave applications fetched successfully",
                        leaveApplicationService.getMyLeaves())
        );
    }

    @GetMapping("/manager/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getPendingLeavesForManager() {

        return ResponseEntity.ok(
                ApiResponse.success(200,
                        "Pending leave applications fetched successfully",
                        leaveApplicationService.getPendingLeavesForManager())
        );
    }
}


