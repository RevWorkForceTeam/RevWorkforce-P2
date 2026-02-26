package com.rev.revworkforcep2.dto.response.leave;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TeamLeaveCalenderResponse {
    private Long employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveType;
    private String status;
    
    public Long getDays() {
        if (startDate != null && endDate != null) {
            return ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0L;
    }
}
