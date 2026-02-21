package com.rev.revworkforcep2.dto.response.leave;

import com.rev.revworkforcep2.model.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveApplicationResponse {

    private Long id;
    private Long userId;
    private Long leaveTypeId;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String reason;
    private LeaveStatus status;
}
