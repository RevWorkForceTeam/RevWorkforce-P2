package com.rev.revworkforcep2.service.designation;

import com.rev.revworkforcep2.dto.request.designation.CreateDesignationRequest;
import com.rev.revworkforcep2.dto.request.designation.UpdateDesignationRequest;
import com.rev.revworkforcep2.dto.response.designation.DesignationResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.designation.DesignationMapper;
import com.rev.revworkforcep2.model.Designation;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.DesignationRepository;
import com.rev.revworkforcep2.service.designation.impl.DesignationServiceImpl;
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
class DesignationServiceImplTest {

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private DesignationMapper designationMapper;

    @InjectMocks
    private DesignationServiceImpl service;

    // CREATE SUCCESS
    @Test
    void createDesignation_shouldCreateSuccessfully() {

        CreateDesignationRequest request = new CreateDesignationRequest();
        request.setTitle("Manager");

        Designation designation = new Designation();
        Designation saved = new Designation();
        DesignationResponse response = new DesignationResponse();

        when(designationRepository.existsByTitle("Manager")).thenReturn(false);
        when(designationMapper.toEntity(request)).thenReturn(designation);
        when(designationRepository.save(designation)).thenReturn(saved);
        when(designationMapper.toResponse(saved)).thenReturn(response);

        DesignationResponse result = service.createDesignation(request);

        assertThat(result).isEqualTo(response);
        verify(designationRepository).save(designation);
    }

    // CREATE DUPLICATE
    @Test
    void createDesignation_shouldThrowException_whenTitleExists() {

        CreateDesignationRequest request = new CreateDesignationRequest();
        request.setTitle("Manager");

        when(designationRepository.existsByTitle("Manager")).thenReturn(true);

        assertThatThrownBy(() -> service.createDesignation(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Designation with this title already exists");

        verify(designationRepository, never()).save(any());
    }

    // UPDATE SUCCESS
    @Test
    void updateDesignation_shouldUpdateSuccessfully() {

        Long id = 1L;

        Designation existing = new Designation();
        existing.setTitle("Manager");

        UpdateDesignationRequest request = new UpdateDesignationRequest();
        request.setTitle("Senior Manager");

        Designation updated = new Designation();
        DesignationResponse response = new DesignationResponse();

        when(designationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(designationRepository.existsByTitle("Senior Manager")).thenReturn(false);
        when(designationRepository.save(existing)).thenReturn(updated);
        when(designationMapper.toResponse(updated)).thenReturn(response);

        DesignationResponse result = service.updateDesignation(id, request);

        assertThat(result).isEqualTo(response);

        verify(designationMapper).updateEntityFromRequest(request, existing);
        verify(designationRepository).save(existing);
    }

    // UPDATE NOT FOUND
    @Test
    void updateDesignation_shouldThrowException_whenNotFound() {

        when(designationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateDesignation(1L, new UpdateDesignationRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Designation not found with id: 1");
    }

    // UPDATE DUPLICATE
    @Test
    void updateDesignation_shouldThrowException_whenTitleExists() {

        Long id = 1L;

        Designation existing = new Designation();
        existing.setTitle("Manager");

        UpdateDesignationRequest request = new UpdateDesignationRequest();
        request.setTitle("Senior Manager");

        when(designationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(designationRepository.existsByTitle("Senior Manager")).thenReturn(true);

        assertThatThrownBy(() -> service.updateDesignation(id, request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Designation with this title already exists");

        verify(designationMapper, never()).updateEntityFromRequest(any(), any());
        verify(designationRepository, never()).save(any());
    }

    // GET BY ID SUCCESS
    @Test
    void getDesignationById_shouldReturnResponse() {

        Long id = 1L;

        Designation designation = new Designation();
        DesignationResponse response = new DesignationResponse();

        when(designationRepository.findById(id)).thenReturn(Optional.of(designation));
        when(designationMapper.toResponse(designation)).thenReturn(response);

        DesignationResponse result = service.getDesignationById(id);

        assertThat(result).isEqualTo(response);
    }

    // GET BY ID NOT FOUND
    @Test
    void getDesignationById_shouldThrowException_whenNotFound() {

        when(designationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDesignationById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Designation not found with id: 1");
    }

    // GET ALL
    @Test
    void getAllDesignations_shouldReturnList() {

        Designation designation = new Designation();
        DesignationResponse response = new DesignationResponse();

        when(designationRepository.findAll()).thenReturn(List.of(designation));
        when(designationMapper.toResponse(designation)).thenReturn(response);

        List<DesignationResponse> result = service.getAllDesignations();

        assertThat(result).hasSize(1);

        verify(designationRepository).findAll();
        verify(designationMapper).toResponse(designation);
    }

    // DELETE SUCCESS
    @Test
    void deleteDesignation_shouldDeleteSuccessfully() {

        Long id = 1L;

        Designation designation = new Designation();
        designation.setUsers(List.of());

        when(designationRepository.findById(id)).thenReturn(Optional.of(designation));

        service.deleteDesignation(id);

        verify(designationRepository).delete(designation);
    }

    // DELETE WITH USERS
    @Test
    void deleteDesignation_shouldThrowException_whenUsersExist() {

        Long id = 1L;

        Designation designation = new Designation();
        designation.setUsers(List.of(new User()));

        when(designationRepository.findById(id)).thenReturn(Optional.of(designation));

        assertThatThrownBy(() -> service.deleteDesignation(id))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Cannot delete designation assigned to users");

        verify(designationRepository, never()).delete(any());
    }
}