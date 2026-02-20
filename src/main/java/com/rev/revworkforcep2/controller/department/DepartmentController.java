package com.rev.revworkforcep2.controller.department;

import com.rev.revworkforcep2.dto.request.department.CreateDepartmentRequest;
import com.rev.revworkforcep2.dto.request.department.UpdateDepartmentRequest;
import com.rev.revworkforcep2.dto.response.department.DepartmentResponse;
import com.rev.revworkforcep2.service.department.DepartmentService;
import com.rev.revworkforcep2.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @RequestBody CreateDepartmentRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Department created",
                        departmentService.createDepartment(request))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @RequestBody UpdateDepartmentRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Department updated",
                        departmentService.updateDepartment(id, request))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Department fetched",
                        departmentService.getDepartmentById(id))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Departments fetched",
                        departmentService.getAllDepartments())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Department deleted", null)
        );
    }
}