package com.rev.revworkforcep2.service.department;

import com.rev.revworkforcep2.dto.request.department.CreateDepartmentRequest;
import com.rev.revworkforcep2.dto.request.department.UpdateDepartmentRequest;
import com.rev.revworkforcep2.dto.response.department.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request);

    DepartmentResponse getDepartmentById(Long id);

    List<DepartmentResponse> getAllDepartments();

    void deleteDepartment(Long id);
}

