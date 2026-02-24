package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.request.leave.CreateHolidayRequest;
import com.rev.revworkforcep2.dto.request.leave.UpdateHolidayRequest;
import com.rev.revworkforcep2.dto.response.leave.HolidayResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.Holiday;
import com.rev.revworkforcep2.repository.HolidayRepository;
import com.rev.revworkforcep2.service.leave.impl.HolidayServiceImpl;
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
class HolidayServiceImplTest {

    @Mock private HolidayRepository holidayRepository;
    @Mock private LeaveMapper leaveMapper;

    @InjectMocks
    private HolidayServiceImpl service;

    // CREATE SUCCESS
    @Test
    void createHoliday_shouldCreateSuccessfully() {

        CreateHolidayRequest request = new CreateHolidayRequest();

        Holiday entity = new Holiday();
        Holiday saved = new Holiday();
        HolidayResponse response = new HolidayResponse();

        when(leaveMapper.toEntity(request)).thenReturn(entity);
        when(holidayRepository.save(entity)).thenReturn(saved);
        when(leaveMapper.toResponse(saved)).thenReturn(response);

        HolidayResponse result = service.createHoliday(request);

        assertThat(result).isEqualTo(response);
        verify(holidayRepository).save(entity);
    }

    // UPDATE SUCCESS
    @Test
    void updateHoliday_shouldUpdateSuccessfully() {

        UpdateHolidayRequest request = new UpdateHolidayRequest();

        Holiday entity = new Holiday();
        Holiday updated = new Holiday();
        HolidayResponse response = new HolidayResponse();

        when(holidayRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(holidayRepository.save(entity)).thenReturn(updated);
        when(leaveMapper.toResponse(updated)).thenReturn(response);

        HolidayResponse result = service.updateHoliday(1L, request);

        verify(leaveMapper).updateEntity(request, entity);
        verify(holidayRepository).save(entity);
        assertThat(result).isEqualTo(response);
    }

    // UPDATE NOT FOUND
    @Test
    void updateHoliday_shouldThrowException_whenNotFound() {

        when(holidayRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateHoliday(1L, new UpdateHolidayRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Holiday not found");
    }

    // GET BY ID SUCCESS
    @Test
    void getHolidayById_shouldReturnHoliday() {

        Holiday entity = new Holiday();
        HolidayResponse response = new HolidayResponse();

        when(holidayRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(leaveMapper.toResponse(entity)).thenReturn(response);

        HolidayResponse result = service.getHolidayById(1L);

        assertThat(result).isEqualTo(response);
    }

    // GET BY ID NOT FOUND
    @Test
    void getHolidayById_shouldThrowException_whenNotFound() {

        when(holidayRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getHolidayById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Holiday not found");
    }

    // GET ALL
    @Test
    void getAllHolidays_shouldReturnList() {

        Holiday entity = new Holiday();
        HolidayResponse response = new HolidayResponse();

        when(holidayRepository.findAll()).thenReturn(List.of(entity));
        when(leaveMapper.toResponse(entity)).thenReturn(response);

        List<HolidayResponse> result = service.getAllHolidays();

        assertThat(result).hasSize(1);
    }

    // DELETE SUCCESS
    @Test
    void deleteHoliday_shouldDeleteSuccessfully() {

        Holiday entity = new Holiday();

        when(holidayRepository.findById(1L)).thenReturn(Optional.of(entity));

        service.deleteHoliday(1L);

        verify(holidayRepository).delete(entity);
    }

    // DELETE NOT FOUND
    @Test
    void deleteHoliday_shouldThrowException_whenNotFound() {

        when(holidayRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteHoliday(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Holiday not found");
    }
}