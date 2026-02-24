package com.rev.revworkforcep2.controller.notification;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
import com.rev.revworkforcep2.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    // getAll()

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_shouldReturn200() throws Exception {

        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("All notifications fetched successfully"));
    }

    // getMyNotifications()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getMyNotifications_shouldReturn200() throws Exception {

        when(service.getMyNotifications()).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("My notifications fetched successfully"));
    }

    // markAsRead()

    @Test
    @WithMockUser(roles = "MANAGER")
    void markAsRead_shouldReturn200() throws Exception {

        doNothing().when(service).markAsRead(1L);

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Notification marked as read"));
    }

    // getUnreadCount()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getUnreadCount_shouldReturn200() throws Exception {

        when(service.getUnreadCount()).thenReturn(5L);

        mockMvc.perform(get("/api/notifications/me/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Unread count fetched"))
                .andExpect(jsonPath("$.data").value(5));
    }
}