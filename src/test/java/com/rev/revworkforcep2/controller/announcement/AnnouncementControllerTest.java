package com.rev.revworkforcep2.controller.announcement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.revworkforcep2.dto.request.announcement.CreateAnnouncementRequest;
import com.rev.revworkforcep2.dto.request.announcement.UpdateAnnouncementRequest;
import com.rev.revworkforcep2.dto.response.announcement.AnnouncementResponse;
import com.rev.revworkforcep2.service.announcement.AnnouncementService;
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
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnnouncementService service;

    // getAll()

    @Test
    @WithMockUser
    @DisplayName("Should fetch all announcements")
    void getAll_shouldReturn200() throws Exception {

        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Announcements fetched successfully"));
    }

    // getById()

    @Test
    @WithMockUser
    @DisplayName("Should fetch announcement by id")
    void getById_shouldReturn200() throws Exception {

        when(service.getById(1L)).thenReturn(new AnnouncementResponse());

        mockMvc.perform(get("/api/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Announcement fetched successfully"));
    }

    // create()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should create announcement")
    void create_shouldReturn201() throws Exception {

        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        AnnouncementResponse response = new AnnouncementResponse();

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/announcements")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message")
                        .value("Announcement created successfully"));
    }

    // update()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should update announcement")
    void update_shouldReturn200() throws Exception {

        UpdateAnnouncementRequest request = new UpdateAnnouncementRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        AnnouncementResponse response = new AnnouncementResponse();

        when(service.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/announcements/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Announcement updated successfully"));
    }

    // delete()

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should delete announcement")
    void delete_shouldReturn200() throws Exception {

        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Announcement deleted successfully"));
    }
}