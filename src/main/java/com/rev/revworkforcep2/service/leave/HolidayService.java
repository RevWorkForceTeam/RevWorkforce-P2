package com.rev.revworkforcep2.service.leave;

import com.rev.revworkforcep2.dto.request.leave.CreateHolidayRequest;
import com.rev.revworkforcep2.dto.request.leave.UpdateHolidayRequest;
import com.rev.revworkforcep2.dto.response.leave.HolidayResponse;

import java.util.List;

public interface HolidayService {

    HolidayResponse createHoliday(CreateHolidayRequest request);

    HolidayResponse updateHoliday(Long id, UpdateHolidayRequest request);

    HolidayResponse getHolidayById(Long id);

    List<HolidayResponse> getAllHolidays();

    void deleteHoliday(Long id);
}
