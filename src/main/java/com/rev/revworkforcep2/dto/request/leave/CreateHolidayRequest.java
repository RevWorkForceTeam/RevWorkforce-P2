
package com.rev.revworkforcep2.dto.request.leave;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHolidayRequest {
    private LocalDate holidayDate; // camelCase
    private String name;
}
