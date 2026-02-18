package com.rev.revworkforcep2.dto.response.leave;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HolidayResponse {
    private Long id;
    private String name;
    private LocalDate holidayDate; // camelCase
}
