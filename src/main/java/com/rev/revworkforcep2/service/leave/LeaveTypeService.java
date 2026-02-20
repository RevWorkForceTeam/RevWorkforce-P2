package com.rev.revworkforcep2.service.leave;


import com.rev.revworkforcep2.dto.request.leave.CreateLeaveTypeRequest;
import com.rev.revworkforcep2.dto.request.leave.UpdateLeaveTypeRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveTypeResponse;
import com.rev.revworkforcep2.model.LeaveType;

import java.util.List;

public interface LeaveTypeService {

    LeaveTypeResponse createLeaveType(CreateLeaveTypeRequest request);

    LeaveTypeResponse updateLeaveType(Long id, UpdateLeaveTypeRequest request);

    LeaveTypeResponse getLeaveTypeById(Long id);

    List<LeaveTypeResponse> getAllLeaveTypes();

    void deleteLeaveType(Long id);

    //List<LeaveType> getAllLeaveTypeEntities();
}
