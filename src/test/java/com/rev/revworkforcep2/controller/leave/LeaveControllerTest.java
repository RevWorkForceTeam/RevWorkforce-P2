package com.rev.revworkforcep2.controller.leave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.revworkforcep2.dto.request.leave.*;
import com.rev.revworkforcep2.dto.response.leave.*;
import com.rev.revworkforcep2.service.leave.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeaveBalanceService leaveBalanceService;

    @MockBean
    private HolidayService holidayService;

    @MockBean
    private LeaveApplicationService leaveApplicationService;

    @MockBean
    private LeaveTypeService leaveTypeService;

    // createLeaveType()

    @Test
    @WithMockUser(roles = "ADMIN")
    void createLeaveType_shouldReturn200() throws Exception {

        CreateLeaveTypeRequest request = new CreateLeaveTypeRequest();
        when(leaveTypeService.createLeaveType(any()))
                .thenReturn(new LeaveTypeResponse());

        mockMvc.perform(post("/api/leaves/types")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Leave type created successfully"));
    }

    // getAllLeaveTypes()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllLeaveTypes_shouldReturn200() throws Exception {

        when(leaveTypeService.getAllLeaveTypes())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/leaves/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Leave types fetched successfully"));
    }

    // getMyBalances()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getMyBalances_shouldReturn200() throws Exception {

        when(leaveBalanceService.getMyBalances())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/leaves/balance/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("My leave balances fetched successfully"));
    }

    // createHoliday()

    @Test
    @WithMockUser(roles = "ADMIN")
    void createHoliday_shouldReturn200() throws Exception {

        CreateHolidayRequest request = new CreateHolidayRequest();

        when(holidayService.createHoliday(any()))
                .thenReturn(new HolidayResponse());

        mockMvc.perform(post("/api/leaves/holidays")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Holiday created successfully"));
    }

    // applyLeave()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void applyLeave_shouldReturn200() throws Exception {

        ApplyLeaveRequest request = new ApplyLeaveRequest();

        when(leaveApplicationService.applyLeave(any()))
                .thenReturn(new LeaveApplicationResponse());

        mockMvc.perform(post("/api/leaves")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Leave applied successfully"));
    }

    // approveLeave()

    @Test
    @WithMockUser(roles = "MANAGER")
    void approveLeave_shouldReturn200() throws Exception {

        when(leaveApplicationService.approveLeave(1L))
                .thenReturn(new LeaveApplicationResponse());

        mockMvc.perform(put("/api/leaves/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Leave approved successfully"));
    }

    // cancelLeave()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void cancelLeave_shouldReturn200() throws Exception {

        when(leaveApplicationService.cancelLeave(1L))
                .thenReturn(new LeaveApplicationResponse());

        mockMvc.perform(put("/api/leaves/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Leave cancelled successfully"));
    }

    // getTeamCalendar()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getTeamCalendar_shouldReturn200() throws Exception {

        when(leaveApplicationService.getTeamCalendar())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/leaves/team-calendar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Team calendar fetched successfully"));
    }
}