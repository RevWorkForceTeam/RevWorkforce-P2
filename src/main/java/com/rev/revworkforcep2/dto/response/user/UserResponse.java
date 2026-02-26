package com.rev.revworkforcep2.dto.response.user;

import com.rev.revworkforcep2.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String employeeId;
    private String email;

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String emergencyContact;

    private LocalDate joiningDate;
    private Double salary;

    private Role role;
    private boolean active;

    private Long departmentId;
    private String departmentName;

    private Long designationId;
    private String designationTitle;

    private ManagerSummaryResponse manager;


    public String getName() {
        return firstName + " " + lastName;
    }

    public String getDepartment() {
        return departmentName;
    }

    public String getDesignation() {
        return designationTitle;
    }

    public String getManagerName() {
        return manager != null ? manager.getFirstName() + " " + manager.getLastName() : null;
    }
}