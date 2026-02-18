package com.rev.revworkforcep2.dto.request.user;

import com.rev.revworkforcep2.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateUserRequest {

    private String employeeId;
    private String email;
    private String password;

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String emergencyContact;

    private LocalDate joiningDate;
    private Double salary;

    private Role role;

    private Long departmentId;
    private Long designationId;
    private Long managerId; // optional
}

