package com.rev.revworkforcep2.dto.response.user;

import com.rev.revworkforcep2.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryResponse {

    private Long id;

    private String employeeId;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private boolean active;

    private String departmentName;

    private String designationTitle;
}