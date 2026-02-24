package com.rev.revworkforcep2.controller.designation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.revworkforcep2.dto.request.designation.CreateDesignationRequest;
import com.rev.revworkforcep2.dto.request.designation.UpdateDesignationRequest;
import com.rev.revworkforcep2.dto.response.designation.DesignationResponse;
import com.rev.revworkforcep2.service.designation.DesignationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DesignationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DesignationService designationService;

    // createDesignation()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should create designation")
    void createDesignation_shouldReturn200() throws Exception {

        CreateDesignationRequest request = new CreateDesignationRequest();
        DesignationResponse response = new DesignationResponse();

        when(designationService.createDesignation(any())).thenReturn(response);

        mockMvc.perform(post("/api/designations")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Designation created"));
    }

    // updateDesignation()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should update designation")
    void updateDesignation_shouldReturn200() throws Exception {

        UpdateDesignationRequest request = new UpdateDesignationRequest();
        DesignationResponse response = new DesignationResponse();

        when(designationService.updateDesignation(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/designations/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Designation updated"));
    }

    // getDesignationById()

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("MANAGER should fetch designation by id")
    void getDesignationById_shouldReturn200() throws Exception {

        when(designationService.getDesignationById(1L))
                .thenReturn(new DesignationResponse());

        mockMvc.perform(get("/api/designations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Designation fetched"));
    }

    // getAllDesignations()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should fetch all designations")
    void getAllDesignations_shouldReturn200() throws Exception {

        when(designationService.getAllDesignations())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/designations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Designations fetched"));
    }

    // deleteDesignation()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should delete designation")
    void deleteDesignation_shouldReturn200() throws Exception {

        doNothing().when(designationService).deleteDesignation(1L);

        mockMvc.perform(delete("/api/designations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Designation deleted"));
    }
}