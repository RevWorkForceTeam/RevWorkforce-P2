package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;

import com.rev.revworkforcep2.dto.request.leave.UpdateLeaveStatusRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;

import java.util.List;


import com.rev.revworkforcep2.dto.request.leave.ApplyLeaveRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveApplicationResponse;

public interface LeaveApplicationService {

    LeaveApplicationResponse applyLeave(ApplyLeaveRequest request);
}
