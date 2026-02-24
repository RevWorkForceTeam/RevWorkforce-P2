package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.request.leave.CreateLeaveTypeRequest;
import com.rev.revworkforcep2.dto.request.leave.UpdateLeaveTypeRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveTypeResponse;
import com.rev.revworkforcep2.exception.ConflictException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.LeaveType;
import com.rev.revworkforcep2.repository.LeaveTypeRepository;
import com.rev.revworkforcep2.service.leave.impl.LeaveTypeServiceImpl;
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
class LeaveTypeServiceImplTest {

    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private LeaveMapper leaveMapper;

    @InjectMocks
    private LeaveTypeServiceImpl service;

    // CREATE SUCCESS
    @Test
    void createLeaveType_shouldCreateSuccessfully() {

        CreateLeaveTypeRequest request = new CreateLeaveTypeRequest();
        request.setName("Sick Leave");

        LeaveType entity = new LeaveType();
        LeaveType saved = new LeaveType();
        LeaveTypeResponse response = new LeaveTypeResponse();

        when(leaveTypeRepository.existsByName("Sick Leave"))
                .thenReturn(false);
        when(leaveMapper.toEntity(request))
                .thenReturn(entity);
        when(leaveTypeRepository.save(entity))
                .thenReturn(saved);
        when(leaveMapper.toResponse(saved))
                .thenReturn(response);

        LeaveTypeResponse result =
                service.createLeaveType(request);

        assertThat(result).isEqualTo(response);
        verify(leaveTypeRepository).save(entity);
    }

    // CREATE DUPLICATE
    @Test
    void createLeaveType_shouldThrowException_whenDuplicate() {

        CreateLeaveTypeRequest request = new CreateLeaveTypeRequest();
        request.setName("Sick Leave");

        when(leaveTypeRepository.existsByName("Sick Leave"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.createLeaveType(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("LeaveType already exists with name: Sick Leave");

        verify(leaveTypeRepository, never()).save(any());
    }

    // UPDATE SUCCESS
    @Test
    void updateLeaveType_shouldUpdateSuccessfully() {

        Long id = 1L;

        LeaveType entity = new LeaveType();
        entity.setName("Old Name");

        UpdateLeaveTypeRequest request =
                new UpdateLeaveTypeRequest();
        request.setName("New Name");

        LeaveType updated = new LeaveType();
        LeaveTypeResponse response = new LeaveTypeResponse();

        when(leaveTypeRepository.findById(id))
                .thenReturn(Optional.of(entity));
        when(leaveTypeRepository.existsByName("New Name"))
                .thenReturn(false);
        when(leaveTypeRepository.save(entity))
                .thenReturn(updated);
        when(leaveMapper.toResponse(updated))
                .thenReturn(response);

        LeaveTypeResponse result =
                service.updateLeaveType(id, request);

        verify(leaveMapper).updateEntity(request, entity);
        verify(leaveTypeRepository).save(entity);
        assertThat(result).isEqualTo(response);
    }

    // UPDATE NOT FOUND
    @Test
    void updateLeaveType_shouldThrowException_whenNotFound() {

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateLeaveType(1L,
                        new UpdateLeaveTypeRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("LeaveType not found with id: 1");
    }

    // UPDATE DUPLICATE
    @Test
    void updateLeaveType_shouldThrowException_whenDuplicate() {

        Long id = 1L;

        LeaveType entity = new LeaveType();
        entity.setName("Old Name");

        UpdateLeaveTypeRequest request =
                new UpdateLeaveTypeRequest();
        request.setName("New Name");

        when(leaveTypeRepository.findById(id))
                .thenReturn(Optional.of(entity));
        when(leaveTypeRepository.existsByName("New Name"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.updateLeaveType(id, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("LeaveType already exists with name: New Name");
    }

    // GET BY ID SUCCESS
    @Test
    void getLeaveTypeById_shouldReturnLeaveType() {

        LeaveType entity = new LeaveType();
        LeaveTypeResponse response = new LeaveTypeResponse();

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(entity));
        when(leaveMapper.toResponse(entity))
                .thenReturn(response);

        LeaveTypeResponse result =
                service.getLeaveTypeById(1L);

        assertThat(result).isEqualTo(response);
    }

    // GET BY ID NOT FOUND
    @Test
    void getLeaveTypeById_shouldThrowException_whenNotFound() {

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getLeaveTypeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("LeaveType not found with id: 1");
    }

    // GET ALL
    @Test
    void getAllLeaveTypes_shouldReturnList() {

        LeaveType entity = new LeaveType();
        LeaveTypeResponse response = new LeaveTypeResponse();

        when(leaveTypeRepository.findAll())
                .thenReturn(List.of(entity));
        when(leaveMapper.toResponse(entity))
                .thenReturn(response);

        List<LeaveTypeResponse> result =
                service.getAllLeaveTypes();

        assertThat(result).hasSize(1);
    }

    // DELETE SUCCESS
    @Test
    void deleteLeaveType_shouldDeleteSuccessfully() {

        LeaveType entity = new LeaveType();

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(entity));

        service.deleteLeaveType(1L);

        verify(leaveTypeRepository).delete(entity);
    }

    // DELETE NOT FOUND
    @Test
    void deleteLeaveType_shouldThrowException_whenNotFound() {

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.deleteLeaveType(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("LeaveType not found with id: 1");
    }
}