package com.rev.revworkforcep2.service.leave.impl;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ConflictException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.security.util.SecurityUtils;
import com.rev.revworkforcep2.service.activity.ActivityLogService;
import com.rev.revworkforcep2.service.leave.LeaveApplicationService;
import com.rev.revworkforcep2.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApplicationServiceImpl implements LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final LeaveMapper leaveMapper;

    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    // =========================================================
    // APPLY LEAVE
    // =========================================================
    @Override
    public LeaveApplicationResponse applyLeave(ApplyLeaveRequest request) {

        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new BusinessValidationException("From date cannot be after To date");
        }

        User employee = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        long days = ChronoUnit.DAYS.between(
                request.getFromDate(),
                request.getToDate()
        ) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(employee.getId(), leaveType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        if (balance.getRemainingDays() < days) {
            throw new BusinessValidationException("Insufficient leave balance");
        }

        LeaveApplication leave = leaveMapper.toEntity(request, employee, leaveType);
        leave.setStatus(LeaveStatus.PENDING);

        LeaveApplication saved = leaveApplicationRepository.save(leave);

        // 🔥 Activity Log
        activityLogService.log(employee.getId(),
                "Applied leave from " + request.getFromDate() + " to " + request.getToDate());

        // 🔥 Notify Manager
        if (employee.getManager() != null) {
            notificationService.triggerForUser(
                    employee.getManager().getId(),
                    employee.getFirstName() + " applied for leave",
                    "LEAVE"
            );
        }

        return leaveMapper.toResponse(saved);
    }

    // =========================================================
    // APPROVE LEAVE
    // =========================================================
    @Override
    public LeaveApplicationResponse approveLeave(Long leaveId) {

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ConflictException("Only pending leave can be approved");
        }

        String currentUsername = SecurityUtils.getCurrentUsername();

        User manager = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        if (!leave.getUser().getManager().getId().equals(manager.getId())) {
            throw new ConflictException("Not authorized to approve this leave");
        }

        long days = ChronoUnit.DAYS.between(
                leave.getStartDate(),
                leave.getEndDate()
        ) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(
                        leave.getUser().getId(),
                        leave.getLeaveType().getId()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        if (balance.getRemainingDays() < days) {
            throw new BusinessValidationException("Insufficient leave balance");
        }

        balance.setRemainingDays(balance.getRemainingDays() - (int) days);
        leaveBalanceRepository.save(balance);

        leave.setStatus(LeaveStatus.APPROVED);
        leaveApplicationRepository.save(leave);

        // 🔥 Activity Log
        activityLogService.log(manager.getId(),
                "Approved leave for " + leave.getUser().getFirstName());

        // 🔥 Notify Employee
        notificationService.triggerForUser(
                leave.getUser().getId(),
                "Your leave has been approved",
                "LEAVE"
        );

        return leaveMapper.toResponse(leave);
    }

    // =========================================================
    // REJECT LEAVE
    // =========================================================
    @Override
    public LeaveApplicationResponse rejectLeave(Long leaveId, String comment) {

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ConflictException("Only pending leave can be rejected");
        }

        if (comment == null || comment.isBlank()) {
            throw new BusinessValidationException("Rejection comment is mandatory");
        }

        String currentUsername = SecurityUtils.getCurrentUsername();

        User manager = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        if (!leave.getUser().getManager().getId().equals(manager.getId())) {
            throw new ConflictException("Not authorized to reject this leave");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerComment(comment);

        leaveApplicationRepository.save(leave);

        // 🔥 Activity Log
        activityLogService.log(manager.getId(),
                "Rejected leave for " + leave.getUser().getFirstName());

        // 🔥 Notify Employee
        notificationService.triggerForUser(
                leave.getUser().getId(),
                "Your leave has been rejected",
                "LEAVE"
        );

        return leaveMapper.toResponse(leave);
    }

    // =========================================================
    // CANCEL LEAVE
    // =========================================================
    @Override
    public LeaveApplicationResponse cancelLeave(Long leaveId) {

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ConflictException("Only pending leave can be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        leaveApplicationRepository.save(leave);

        // 🔥 Activity Log
        activityLogService.log(leave.getUser().getId(),
                "Cancelled leave request");

        return leaveMapper.toResponse(leave);
    }

    // =========================================================
    // ASSIGN DEFAULT LEAVES
    // =========================================================
    @Override
    public void assignDefaultLeaves(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        for (LeaveType type : leaveTypes) {

            boolean exists = leaveBalanceRepository
                    .findByUserIdAndLeaveTypeId(user.getId(), type.getId())
                    .isPresent();

            if (exists) continue;

            LeaveBalance balance = new LeaveBalance();
            balance.setUser(user);
            balance.setLeaveType(type);
            balance.setTotalDays(type.getDefaultQuota());
            balance.setUsedDays(0);
            balance.setRemainingDays(type.getDefaultQuota());

            leaveBalanceRepository.save(balance);
        }
    }
    @Override
    public List<LeaveApplicationResponse> getMyLeaves() {

        String email = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Logged in user not found"));

        List<LeaveApplication> leaves =
                leaveApplicationRepository.findByUserId(user.getId());

        return leaves.stream()
                .map(leaveMapper::toResponse)
                .toList();
    }
    @Override
    public List<LeaveApplicationResponse> getPendingLeavesForManager() {

        String email = SecurityUtils.getCurrentUsername();

        User manager = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Logged in user not found"));

        List<LeaveApplication> leaves =
                leaveApplicationRepository
                        .findByUserManagerIdAndStatus(
                                manager.getId(),
                                LeaveStatus.PENDING
                        );

        return leaves.stream()
                .map(leaveMapper::toResponse)
                .toList();
    }
}