package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;

import java.util.List;

public interface LeaveApplicationService {

    LeaveApplicationResponse applyLeave(ApplyLeaveRequest request);

    LeaveApplicationResponse approveLeave(Long leaveId);

    LeaveApplicationResponse rejectLeave(Long leaveId, String comment);

    LeaveApplicationResponse cancelLeave(Long leaveId);
    void assignDefaultLeaves(Long employeeId);

    //change by suji
    List<LeaveApplicationResponse> getMyLeaves();

    List<LeaveApplicationResponse> getPendingLeavesForManager();
}

