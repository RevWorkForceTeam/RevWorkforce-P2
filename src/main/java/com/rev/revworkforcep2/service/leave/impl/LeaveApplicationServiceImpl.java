package com.rev.revworkforcep2.service.leave.impl;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.LeaveApplication;
import com.rev.revworkforcep2.model.LeaveStatus;
import com.rev.revworkforcep2.model.LeaveType;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.LeaveApplicationRepository;
import com.rev.revworkforcep2.repository.LeaveTypeRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.leave.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveApplicationServiceImpl implements LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;
    private final LeaveMapper leaveMapper;

    @Override
    public LeaveApplicationResponse applyLeave(ApplyLeaveRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        LeaveApplication entity = leaveMapper.toEntity(request, user, leaveType);

        entity.setStatus(LeaveStatus.PENDING);


        return leaveMapper.toResponse(
                leaveApplicationRepository.save(entity)
        );
    }
}
