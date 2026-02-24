package com.rev.revworkforcep2.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.revworkforcep2.dto.request.auth.LoginRequest;
import com.rev.revworkforcep2.dto.response.auth.LoginResponse;
import com.rev.revworkforcep2.service.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // login()

    @Test
    @DisplayName("Should login successfully")
    void login_shouldReturn200() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("password");

        LoginResponse response = Mockito.mock(LoginResponse.class);
        when(response.getToken()).thenReturn("dummy-jwt-token");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("dummy-jwt-token"));
    }

    @Test
    @DisplayName("Should return 400 when validation fails")
    void login_shouldReturn400_whenInvalidRequest() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail(null);
        request.setPassword(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}