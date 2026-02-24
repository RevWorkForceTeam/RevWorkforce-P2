package com.rev.revworkforcep2.controller.activity;

import com.rev.revworkforcep2.service.activity.ActivityLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityLogService activityService;


    // getAllActivities()


    @Test
    @DisplayName("ADMIN should fetch all activities successfully")
    @WithMockUser(roles = "ADMIN")
    void getAllActivities_shouldReturn200_forAdmin() throws Exception {

        when(activityService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Activities fetched successfully"));
    }


    // getByUser()


    @Test
    @DisplayName("EMPLOYEE should fetch their activities successfully")
    @WithMockUser(roles = "EMPLOYEE")
    void getByUser_shouldReturn200_forEmployee() throws Exception {

        when(activityService.getByUser(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/activity/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("User activities fetched successfully"));
    }
}