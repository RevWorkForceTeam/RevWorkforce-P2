package com.rev.revworkforcep2.dto.request.leave;

import com.rev.revworkforcep2.model.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ApplyLeaveRequest {

    private Long userId;
    private Long leaveTypeId;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String reason;


}

