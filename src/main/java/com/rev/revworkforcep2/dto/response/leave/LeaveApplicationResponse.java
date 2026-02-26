package com.rev.revworkforcep2.dto.response.leave;

import com.rev.revworkforcep2.model.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class LeaveApplicationResponse {

    private Long id;
    private Long userId;
    private Long leaveTypeId;
    private String leaveType;
    private String employeeName;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long numberOfDays;
    private LocalDateTime appliedDate;

    private String reason;
    private LeaveStatus status;
    private String managerComment;

    public Long getDays() {
        if (startDate != null && endDate != null) {
            return ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0L;
    }
}
