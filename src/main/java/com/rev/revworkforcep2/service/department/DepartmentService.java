package com.rev.revworkforcep2.service.department;

import com.rev.revworkforcep2.dto.request.department.CreateDepartmentRequest;
import com.rev.revworkforcep2.dto.request.department.UpdateDepartmentRequest;
import com.rev.revworkforcep2.dto.response.department.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    // Create
    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    // Update
    DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request);

    // Get by ID
    DepartmentResponse getDepartmentById(Long id);

    // Get All
    List<DepartmentResponse> getAllDepartments();

    // Delete
    void deleteDepartment(Long id);
}

