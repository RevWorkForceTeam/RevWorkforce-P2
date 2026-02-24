package com.rev.revworkforcep2.controller.department;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.revworkforcep2.dto.request.department.CreateDepartmentRequest;
import com.rev.revworkforcep2.dto.request.department.UpdateDepartmentRequest;
import com.rev.revworkforcep2.dto.response.department.DepartmentResponse;
import com.rev.revworkforcep2.service.department.DepartmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    // createDepartment()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should create department")
    void createDepartment_shouldReturn200() throws Exception {

        CreateDepartmentRequest request = new CreateDepartmentRequest();
        DepartmentResponse response = new DepartmentResponse();

        when(departmentService.createDepartment(any())).thenReturn(response);

        mockMvc.perform(post("/api/departments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Department created"));
    }

    // updateDepartment()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should update department")
    void updateDepartment_shouldReturn200() throws Exception {

        UpdateDepartmentRequest request = new UpdateDepartmentRequest();
        DepartmentResponse response = new DepartmentResponse();

        when(departmentService.updateDepartment(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/departments/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Department updated"));
    }

    // getDepartmentById()

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("MANAGER should fetch department by id")
    void getDepartmentById_shouldReturn200() throws Exception {

        when(departmentService.getDepartmentById(1L))
                .thenReturn(new DepartmentResponse());

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Department fetched"));
    }

    // getAllDepartments()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should fetch all departments")
    void getAllDepartments_shouldReturn200() throws Exception {

        when(departmentService.getAllDepartments())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Departments fetched"));
    }

    // deleteDepartment()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should delete department")
    void deleteDepartment_shouldReturn200() throws Exception {

        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Department deleted"));
    }
}