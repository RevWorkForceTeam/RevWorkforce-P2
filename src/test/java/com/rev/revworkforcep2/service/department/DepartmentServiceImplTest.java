package com.rev.revworkforcep2.service.department;

import com.rev.revworkforcep2.dto.request.department.CreateDepartmentRequest;
import com.rev.revworkforcep2.dto.request.department.UpdateDepartmentRequest;
import com.rev.revworkforcep2.dto.response.department.DepartmentResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.department.DepartmentMapper;
import com.rev.revworkforcep2.model.Department;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.DepartmentRepository;
import com.rev.revworkforcep2.service.department.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl service;

    // CREATE SUCCESS
    @Test
    void createDepartment_shouldCreateSuccessfully() {

        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setName("HR");

        Department department = new Department();
        Department saved = new Department();
        DepartmentResponse response = new DepartmentResponse();

        when(departmentRepository.existsByName("HR")).thenReturn(false);
        when(departmentMapper.toEntity(request)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(saved);
        when(departmentMapper.toResponse(saved)).thenReturn(response);

        DepartmentResponse result = service.createDepartment(request);

        assertThat(result).isEqualTo(response);
        verify(departmentRepository).save(department);
    }

    // CREATE DUPLICATE
    @Test
    void createDepartment_shouldThrowException_whenNameExists() {

        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setName("HR");

        when(departmentRepository.existsByName("HR")).thenReturn(true);

        assertThatThrownBy(() -> service.createDepartment(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Department with this name already exists");

        verify(departmentRepository, never()).save(any());
    }

    // UPDATE SUCCESS
    @Test
    void updateDepartment_shouldUpdateSuccessfully() {

        Long id = 1L;

        Department existing = new Department();
        existing.setName("HR");

        UpdateDepartmentRequest request = new UpdateDepartmentRequest();
        request.setName("Finance");

        Department updated = new Department();
        DepartmentResponse response = new DepartmentResponse();

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.existsByName("Finance")).thenReturn(false);
        when(departmentRepository.save(existing)).thenReturn(updated);
        when(departmentMapper.toResponse(updated)).thenReturn(response);

        DepartmentResponse result = service.updateDepartment(id, request);

        assertThat(result).isEqualTo(response);

        verify(departmentMapper).updateEntityFromRequest(request, existing);
        verify(departmentRepository).save(existing);
    }

    // UPDATE NOT FOUND
    @Test
    void updateDepartment_shouldThrowException_whenNotFound() {

        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateDepartment(1L, new UpdateDepartmentRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department not found with id: 1");
    }

    // UPDATE DUPLICATE
    @Test
    void updateDepartment_shouldThrowException_whenNameExists() {

        Long id = 1L;

        Department existing = new Department();
        existing.setName("HR");

        UpdateDepartmentRequest request = new UpdateDepartmentRequest();
        request.setName("Finance");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.existsByName("Finance")).thenReturn(true);

        assertThatThrownBy(() -> service.updateDepartment(id, request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Department with this name already exists");

        verify(departmentMapper, never()).updateEntityFromRequest(any(), any());
        verify(departmentRepository, never()).save(any());
    }

    // GET BY ID SUCCESS
    @Test
    void getDepartmentById_shouldReturnResponse() {

        Long id = 1L;

        Department department = new Department();
        DepartmentResponse response = new DepartmentResponse();

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(departmentMapper.toResponse(department)).thenReturn(response);

        DepartmentResponse result = service.getDepartmentById(id);

        assertThat(result).isEqualTo(response);
    }

    // GET BY ID NOT FOUND
    @Test
    void getDepartmentById_shouldThrowException_whenNotFound() {

        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDepartmentById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Department not found with id: 1");
    }

    // GET ALL
    @Test
    void getAllDepartments_shouldReturnList() {

        Department department = new Department();
        DepartmentResponse response = new DepartmentResponse();

        when(departmentRepository.findAll()).thenReturn(List.of(department));
        when(departmentMapper.toResponse(department)).thenReturn(response);

        List<DepartmentResponse> result = service.getAllDepartments();

        assertThat(result).hasSize(1);

        verify(departmentRepository).findAll();
        verify(departmentMapper).toResponse(department);
    }

    // DELETE SUCCESS
    @Test
    void deleteDepartment_shouldDeleteSuccessfully() {

        Long id = 1L;

        Department department = new Department();
        department.setUsers(List.of());

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        service.deleteDepartment(id);

        verify(departmentRepository).delete(department);
    }

    // DELETE WITH USERS
    @Test
    void deleteDepartment_shouldThrowException_whenUsersExist() {

        Long id = 1L;

        Department department = new Department();
        department.setUsers(List.of(new User()));

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> service.deleteDepartment(id))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Cannot delete department with assigned users");

        verify(departmentRepository, never()).delete(any());
    }
}