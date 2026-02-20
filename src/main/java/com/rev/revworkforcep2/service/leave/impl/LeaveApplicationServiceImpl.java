//package com.rev.revworkforcep2.service.leave.impl;
//
//import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
//import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;
//import com.rev.revworkforcep2.exception.BusinessValidationException;
//import com.rev.revworkforcep2.exception.ConflictException;
//import com.rev.revworkforcep2.exception.ResourceNotFoundException;
//import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
//import com.rev.revworkforcep2.model.*;
//import com.rev.revworkforcep2.repository.*;
//import com.rev.revworkforcep2.service.leave.LeaveApplicationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class LeaveApplicationServiceImpl implements LeaveApplicationService {
//
//    private final LeaveApplicationRepository leaveApplicationRepository;
//    private final LeaveTypeRepository leaveTypeRepository;
//    private final LeaveBalanceRepository leaveBalanceRepository;
//    private final UserRepository userRepository;
//    private final LeaveMapper leaveMapper;
//
//    @Override
//    public LeaveApplicationResponse applyLeave(ApplyLeaveRequest request) {
//
//        if (request == null) {
//            throw new BusinessValidationException("Request cannot be null");
//        }
//
//        if (request.getFromDate().isAfter(request.getToDate())) {
//            throw new BusinessValidationException("From date cannot be after To date");
//        }
//
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
//                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));
//
//        LeaveApplication leave = leaveMapper.toEntity(request, user, leaveType);
//        leave.setStatus(LeaveStatus.PENDING);
//
//        LeaveApplication saved = leaveApplicationRepository.save(leave);
//
//        return leaveMapper.toResponse(saved);
//    }
//
//    @Override
//    public LeaveApplicationResponse approveLeave(Long leaveId) {
//
//        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
//                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
//
//        if (leave.getStatus() != LeaveStatus.PENDING) {
//            throw new ConflictException("Only pending leave can be approved");
//        }
//
//        long days = ChronoUnit.DAYS.between(
//                leave.getStartDate(),
//                leave.getEndDate()
//        ) + 1;
//
//
//        LeaveBalance balance = leaveBalanceRepository
//                .findByUserIdAndLeaveTypeId(
//                        leave.getUser().getId(),
//                        leave.getLeaveType().getId()
//                )
//                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));
//
//        if (balance.getRemainingDays() < days) {
//            throw new BusinessValidationException("Insufficient leave balance");
//        }
//
//        balance.setRemainingDays(balance.getRemainingDays() - (int) days);
//        leaveBalanceRepository.save(balance);
//
//        leave.setStatus(LeaveStatus.APPROVED);
//        leaveApplicationRepository.save(leave);
//
//        return leaveMapper.toResponse(leave);
//    }
//
//    @Override
//    public LeaveApplicationResponse rejectLeave(Long leaveId, String comment) {
//
//        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
//                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
//
//        if (leave.getStatus() != LeaveStatus.PENDING) {
//            throw new ConflictException("Only pending leave can be rejected");
//        }
//
//        leave.setStatus(LeaveStatus.REJECTED);
//        leave.setManagerComment(comment);
//
//        leaveApplicationRepository.save(leave);
//
//        return leaveMapper.toResponse(leave);
//    }
//
//    @Override
//    public LeaveApplicationResponse cancelLeave(Long leaveId) {
//
//        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
//                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
//
//        if (leave.getStatus() == LeaveStatus.APPROVED) {
//            throw new ConflictException("Approved leave cannot be cancelled");
//        }
//
//        leave.setStatus(LeaveStatus.CANCELLED);
//        leaveApplicationRepository.save(leave);
//
//        return leaveMapper.toResponse(leave);
//    }
//    @Override
//    public void assignDefaultLeaves(Long employeeId) {
//        User employee = userRepository.findById(employeeId)
//                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
//
//        List<LeaveType> leaveTypes = leaveTypeRepository.findAll(); // use repo directly
//
//        for (LeaveType type : leaveTypes) {
//            LeaveBalance balance = new LeaveBalance();
//            balance.setUser(employee);
//            balance.setLeaveType(type);
//            balance.setTotalDays(type.getDefaultQuota());
//            balance.setUsedDays(0);
//            balance.setRemainingDays(type.getDefaultQuota());
//
//            leaveBalanceRepository.save(balance);
//        }
//    }
//}
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
import com.rev.revworkforcep2.service.leave.LeaveApplicationService;
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

    // =========================================================
    // APPLY LEAVE
    // =========================================================
    @Override
    public LeaveApplicationResponse applyLeave(ApplyLeaveRequest request) {

        if (request == null) {
            throw new BusinessValidationException("Request cannot be null");
        }

        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new BusinessValidationException("From date cannot be after To date");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        long days = ChronoUnit.DAYS.between(
                request.getFromDate(),
                request.getToDate()
        ) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeId(user.getId(), leaveType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        if (balance.getRemainingDays() < days) {
            throw new BusinessValidationException("Insufficient leave balance");
        }

        LeaveApplication leave = leaveMapper.toEntity(request, user, leaveType);
        leave.setStatus(LeaveStatus.PENDING);

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

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ConflictException("Only pending leave can be approved");
        }

        // 🔐 Manager Authorization
        String currentUsername = SecurityUtils.getCurrentUsername();

        User manager = userRepository.findByEmail(currentUsername) // or findByUsername
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        if (leave.getUser().getManager() == null ||
                !leave.getUser().getManager().getId().equals(manager.getId())) {
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

        if (leave.getUser().getManager() == null ||
                !leave.getUser().getManager().getId().equals(manager.getId())) {
            throw new ConflictException("Not authorized to reject this leave");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerComment(comment);

        leaveApplicationRepository.save(leave);

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

            if (exists) {
                continue;
            }

            LeaveBalance balance = new LeaveBalance();
            balance.setUser(user);
            balance.setLeaveType(type);
            balance.setTotalDays(type.getDefaultQuota());
            balance.setUsedDays(0);
            balance.setRemainingDays(type.getDefaultQuota());

            leaveBalanceRepository.save(balance);
        }
    }
}