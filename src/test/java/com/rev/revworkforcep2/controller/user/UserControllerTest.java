package com.rev.revworkforcep2.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.revworkforcep2.dto.request.user.*;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.dto.response.user.UserSummaryResponse;
import com.rev.revworkforcep2.service.user.UserService;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // createUser()

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_shouldReturn200() throws Exception {

        when(userService.createUser(any()))
                .thenReturn(new UserResponse());

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CreateUserRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User created"));
    }

    // updateUser()

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldReturn200() throws Exception {

        when(userService.updateUser(eq(1L), any()))
                .thenReturn(new UserResponse());

        mockMvc.perform(put("/api/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdateUserRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated"));
    }

    // assignManager()

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignManager_shouldReturn200() throws Exception {

        AssignManagerRequest request = new AssignManagerRequest();
        request.setUserId(1L);
        request.setManagerId(2L);

        when(userService.assignManager(1L, 2L))
                .thenReturn(new UserResponse());

        mockMvc.perform(put("/api/users/assign-manager")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Manager assigned"));
    }

    // deactivateUser()

    @Test
    @WithMockUser(roles = "ADMIN")
    void deactivateUser_shouldReturn200() throws Exception {

        doNothing().when(userService).deactivateUser(1L);

        mockMvc.perform(put("/api/users/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deactivated"));
    }

    // reactivateUser()

    @Test
    @WithMockUser(roles = "ADMIN")
    void reactivateUser_shouldReturn200() throws Exception {

        doNothing().when(userService).reactivateUser(1L);

        mockMvc.perform(put("/api/users/1/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User reactivated"));
    }

    // getUserById()

    @Test
    @WithMockUser(roles = "MANAGER")
    void getUserById_shouldReturn200() throws Exception {

        when(userService.getUserById(1L))
                .thenReturn(new UserResponse());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User fetched"));
    }

    // getAllUsers()

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturn200() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users fetched"));
    }

    // getUsersByDepartment()

    @Test
    @WithMockUser(roles = "MANAGER")
    void getUsersByDepartment_shouldReturn200() throws Exception {

        when(userService.getUsersByDepartment(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/users/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users fetched"));
    }

    // getUsersByManager()

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersByManager_shouldReturn200() throws Exception {

        when(userService.getUsersByManager(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/users/manager/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users fetched"));
    }

    // filterUsers()

    @Test
    @WithMockUser(roles = "ADMIN")
    void filterUsers_shouldReturn200() throws Exception {

        when(userService.filterUsers(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/users/filter")
                        .param("departmentId", "1")
                        .param("designationId", "2")
                        .param("active", "true")
                        .param("role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Filtered users"));
    }
}