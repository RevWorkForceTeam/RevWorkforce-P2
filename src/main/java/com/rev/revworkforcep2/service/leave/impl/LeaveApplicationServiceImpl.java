package com.rev.revworkforcep2.service.leave.impl;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;
import com.rev.revworkforcep2.dto.response.leave.TeamLeaveCalenderResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ConflictException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.security.util.SecurityUtils;
import com.rev.revworkforcep2.service.leave.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class LeaveApplicationServiceImpl implements LeaveApplicationService {
    private final HolidayRepository holidayRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final LeaveMapper leaveMapper;

    // =========================================================
    // APPLY LEAVE (Logged-in user only)
    // =========================================================
    @Override
    public LeaveApplicationResponse applyLeave(ApplyLeaveRequest request) {

        if (request == null)
            throw new BusinessValidationException("Request cannot be null");

        if (request.getFromDate().isAfter(request.getToDate()))
            throw new BusinessValidationException("From date cannot be after To date");

        User user = userRepository.findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        long days = ChronoUnit.DAYS.between(request.getFromDate(), request.getToDate()) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(user.getId(), leaveType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        if (balance.getRemainingDays() < days)
            throw new BusinessValidationException("Insufficient leave balance");

        LeaveApplication leave = leaveMapper.toEntity(request, user, leaveType);
        leave.setStatus(LeaveStatus.PENDING);

        // Assign manager if employee
        if (user.getRole() == Role.EMPLOYEE && user.getManager() != null) {
            leave.setManager(user.getManager());
        }

        LeaveApplication saved = leaveApplicationRepository.save(leave);

        return leaveMapper.toResponse(saved);
    }

    // =========================================================
    // APPROVE LEAVE
    // =========================================================
    @Override
    public LeaveApplicationResponse approveLeave(Long leaveId) {

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveStatus.PENDING)
            throw new ConflictException("Only pending leave can be approved");

        User currentUser = userRepository.findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        User leaveOwner = leave.getUser();

        if (!isAuthorizedToApprove(leaveOwner, leave, currentUser)) {
            throw new ConflictException("Not authorized to approve this leave");
        }

        long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(leaveOwner.getId(), leave.getLeaveType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        balance.setUsedDays(balance.getUsedDays() + (int) days);
        balance.setRemainingDays(balance.getTotalDays() - balance.getUsedDays());
        leaveBalanceRepository.save(balance);

        leave.setStatus(LeaveStatus.APPROVED);
        leaveApplicationRepository.save(leave);

        return leaveMapper.toResponse(leave);
    }

    // =========================================================
    // REJECT LEAVE
    // =========================================================
    @Override
    public LeaveApplicationResponse rejectLeave(Long leaveId, String comment) {

        if (comment == null || comment.isBlank())
            throw new BusinessValidationException("Rejection comment is mandatory");

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveStatus.PENDING)
            throw new ConflictException("Only pending leave can be rejected");

        User currentUser = userRepository.findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        User leaveOwner = leave.getUser();

        if (!isAuthorizedToApprove(leaveOwner, leave, currentUser)) {
            throw new ConflictException("Not authorized to reject this leave");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerComment(comment);
        leaveApplicationRepository.save(leave);

        return leaveMapper.toResponse(leave);
    }

    // =========================================================
    // CANCEL LEAVE (Owner only)
    // =========================================================
    @Override
    public LeaveApplicationResponse cancelLeave(Long leaveId) {

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() != LeaveStatus.PENDING)
            throw new ConflictException("Only pending leave can be cancelled");

        User user = userRepository.findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!leave.getUser().getId().equals(user.getId()))
            throw new ConflictException("You can only cancel your own leave");

        leave.setStatus(LeaveStatus.CANCELLED);
        leaveApplicationRepository.save(leave);

        return leaveMapper.toResponse(leave);
    }

    // =========================================================
    // GET MY LEAVES
    // =========================================================
    @Override
    public List<LeaveApplicationResponse> getMyLeaves() {

        User user = userRepository.findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return leaveApplicationRepository.findByUserId(user.getId())
                .stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    // =========================================================
    // GET PENDING LEAVES FOR LOGGED-IN MANAGER
    // =========================================================
    @Override
    public List<LeaveApplicationResponse> getPendingLeavesForManager() {

        User manager = userRepository.findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return leaveApplicationRepository
                .findByUserManagerIdAndStatus(manager.getId(), LeaveStatus.PENDING)
                .stream()
                .map(leaveMapper::toResponse)
                .toList();
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

    // =========================================================
    // PRIVATE AUTHORIZATION CHECK
    // =========================================================
    private boolean isAuthorizedToApprove(User leaveOwner,
                                          LeaveApplication leave,
                                          User currentUser) {

        // Employee leave → manager approves
        if (leaveOwner.getRole() == Role.EMPLOYEE &&
                leave.getManager() != null &&
                leave.getManager().getId().equals(currentUser.getId())) {
            return true;
        }

        // Manager leave → admin approves
        if (leaveOwner.getRole() == Role.MANAGER &&
                currentUser.getRole() == Role.ADMIN) {
            return true;
        }

        // Admin leave → admin approves
        return leaveOwner.getRole() == Role.ADMIN &&
                currentUser.getRole() == Role.ADMIN;
    }
    @Override
    public List<TeamLeaveCalenderResponse> getTeamCalendar() {

        User currentUser = userRepository
                .findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<LeaveApplication> leaves;

        if (currentUser.getRole() == Role.MANAGER) {
            leaves = leaveApplicationRepository.findByUserManagerId(currentUser.getId());
        }
        else if (currentUser.getRole() == Role.ADMIN) {
            leaves = leaveApplicationRepository.findAll();
        }
        else {
            leaves = leaveApplicationRepository.findByUserId(currentUser.getId());
        }

        List<TeamLeaveCalenderResponse> leaveDtos =
                leaves.stream()
                        .map(leaveMapper::toTeamCalendar)
                        .toList();

        List<TeamLeaveCalenderResponse> holidayDtos =
                holidayRepository.findAll()
                        .stream()
                        .map(leaveMapper::holidayToTeamCalendar)
                        .toList();

        List<TeamLeaveCalenderResponse> combined = new ArrayList<>();
        combined.addAll(leaveDtos);
        combined.addAll(holidayDtos);

        return combined;
    }
}