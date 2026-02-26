package com.rev.revworkforcep2.service.leave.impl;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;
import com.rev.revworkforcep2.dto.response.leave.LeaveReportResponse;
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
import java.util.Map;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class LeaveApplicationServiceImpl implements LeaveApplicationService {
    private final HolidayRepository holidayRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final LeaveMapper leaveMapper;
    private final com.rev.revworkforcep2.service.notification.NotificationService notificationService;


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


        if (user.getRole() == Role.EMPLOYEE && user.getManager() != null) {
            leave.setManager(user.getManager());
        }

        LeaveApplication saved = leaveApplicationRepository.save(leave);


        if (user.getManager() != null) {
            notificationService.triggerForUser(
                    user.getManager().getId(),
                    "<strong>" + user.getFirstName() + " " + user.getLastName() + "</strong> applied for <strong>" + leaveType.getName() + "</strong>.",
                    "LEAVE"
            );
        }

        return leaveMapper.toResponse(saved);
    }


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
        leave.setManagerComment("Approved");
        leaveApplicationRepository.save(leave);

        notificationService.triggerForUser(
                leaveOwner.getId(),
                "Your <strong>" + leave.getLeaveType().getName() + "</strong> has been <strong style='color:#059669'>approved</strong>.",
                "LEAVE"
        );

        return leaveMapper.toResponse(leave);
    }


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

        notificationService.triggerForUser(
                leaveOwner.getId(),
                "Your <strong>" + leave.getLeaveType().getName() + "</strong> has been <strong style='color:#dc2626'>rejected</strong>.",
                "LEAVE"
        );

        return leaveMapper.toResponse(leave);
    }


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

    @Override
    public List<LeaveApplicationResponse> getMyLeaves() {

        User user = userRepository.findByEmail(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return leaveApplicationRepository.findByUserId(user.getId())
                .stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

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
            

            int quota = getRoleBasedQuota(user.getRole(), type.getName());
            balance.setTotalDays(quota);
            balance.setUsedDays(0);
            balance.setRemainingDays(quota);

            leaveBalanceRepository.save(balance);
        }
    }
    
    private int getRoleBasedQuota(Role role, String leaveTypeName) {
        return switch (role) {
            case ADMIN -> switch (leaveTypeName) {
                case "Sick Leave" -> 15;
                case "Casual Leave" -> 20;
                case "Annual Leave" -> 30;
                default -> 12;
            };
            case MANAGER -> switch (leaveTypeName) {
                case "Sick Leave" -> 12;
                case "Casual Leave" -> 18;
                case "Annual Leave" -> 25;
                default -> 10;
            };
            case EMPLOYEE -> switch (leaveTypeName) {
                case "Sick Leave" -> 10;
                case "Casual Leave" -> 15;
                case "Annual Leave" -> 21;
                default -> 8;
            };
        };
    }


    private boolean isAuthorizedToApprove(User leaveOwner,
                                          LeaveApplication leave,
                                          User currentUser) {


        if (leaveOwner.getRole() == Role.EMPLOYEE &&
                leave.getManager() != null &&
                leave.getManager().getId().equals(currentUser.getId())) {
            return true;
        }


        if (leaveOwner.getRole() == Role.MANAGER &&
                currentUser.getRole() == Role.ADMIN) {
            return true;
        }


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

    @Override
    public List<LeaveApplicationResponse> getAllLeaves() {
        return leaveApplicationRepository.findAll()
                .stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    @Override
    public List<LeaveReportResponse> getDepartmentWiseReport() {
        List<User> users = userRepository.findAll();
        Map<String, List<User>> usersByDept = users.stream()
                .collect(Collectors.groupingBy(u -> 
                    u.getDepartment() != null ? u.getDepartment().getName() : "No Department"
                ));

        List<LeaveReportResponse> reports = new ArrayList<>();
        for (Map.Entry<String, List<User>> entry : usersByDept.entrySet()) {
            String deptName = entry.getKey();
            List<Long> userIds = entry.getValue().stream().map(User::getId).toList();
            List<LeaveApplication> leaves = leaveApplicationRepository.findAll().stream()
                    .filter(l -> userIds.contains(l.getUser().getId()))
                    .toList();

            LeaveReportResponse report = new LeaveReportResponse();
            report.setName(deptName);
            report.setDepartment(deptName);
            report.setTotalLeaves(leaves.size());
            report.setApprovedLeaves((int) leaves.stream().filter(l -> l.getStatus() == LeaveStatus.APPROVED).count());
            report.setPendingLeaves((int) leaves.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count());
            report.setRejectedLeaves((int) leaves.stream().filter(l -> l.getStatus() == LeaveStatus.REJECTED).count());
            report.setCasualLeaves((int) leaves.stream().filter(l -> "Casual Leave".equals(l.getLeaveType().getName())).count());
            report.setSickLeaves((int) leaves.stream().filter(l -> "Sick Leave".equals(l.getLeaveType().getName())).count());
            report.setPaidLeaves((int) leaves.stream().filter(l -> "Paid Leave".equals(l.getLeaveType().getName())).count());
            reports.add(report);
        }
        return reports;
    }

    @Override
    public List<LeaveReportResponse> getEmployeeWiseReport() {
        List<User> users = userRepository.findAll();
        List<LeaveReportResponse> reports = new ArrayList<>();

        for (User user : users) {
            List<LeaveApplication> leaves = leaveApplicationRepository.findByUserId(user.getId());
            
            LeaveReportResponse report = new LeaveReportResponse();
            report.setName(user.getFirstName() + " " + user.getLastName());
            report.setDepartment(user.getDepartment() != null ? user.getDepartment().getName() : "N/A");
            report.setTotalLeaves(leaves.size());
            report.setApprovedLeaves((int) leaves.stream().filter(l -> l.getStatus() == LeaveStatus.APPROVED).count());
            report.setPendingLeaves((int) leaves.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count());
            report.setRejectedLeaves((int) leaves.stream().filter(l -> l.getStatus() == LeaveStatus.REJECTED).count());
            report.setCasualLeaves((int) leaves.stream().filter(l -> "Casual Leave".equals(l.getLeaveType().getName())).count());
            report.setSickLeaves((int) leaves.stream().filter(l -> "Sick Leave".equals(l.getLeaveType().getName())).count());
            report.setPaidLeaves((int) leaves.stream().filter(l -> "Paid Leave".equals(l.getLeaveType().getName())).count());
            reports.add(report);
        }
        return reports;
    }
}