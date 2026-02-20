package com.rev.revworkforcep2.controller.designation;

import com.rev.revworkforcep2.dto.request.designation.CreateDesignationRequest;
import com.rev.revworkforcep2.dto.request.designation.UpdateDesignationRequest;
import com.rev.revworkforcep2.dto.response.designation.DesignationResponse;
import com.rev.revworkforcep2.service.designation.DesignationService;
import com.rev.revworkforcep2.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService designationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<DesignationResponse>> createDesignation(
            @RequestBody CreateDesignationRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Designation created",
                        designationService.createDesignation(request))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesignationResponse>> updateDesignation(
            @PathVariable Long id,
            @RequestBody UpdateDesignationRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Designation updated",
                        designationService.updateDesignation(id, request))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DesignationResponse>> getDesignationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Designation fetched",
                        designationService.getDesignationById(id))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DesignationResponse>>> getAllDesignations() {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Designations fetched",
                        designationService.getAllDesignations())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDesignation(
            @PathVariable Long id) {

        designationService.deleteDesignation(id);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Designation deleted", null)
        );
    }
}