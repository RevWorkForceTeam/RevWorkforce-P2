package com.rev.revworkforcep2.controller.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.revworkforcep2.dto.request.performance.*;
import com.rev.revworkforcep2.dto.response.performance.*;
import com.rev.revworkforcep2.service.performance.GoalService;
import com.rev.revworkforcep2.service.performance.PerformanceReviewService;
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
class PerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PerformanceReviewService reviewService;

    @MockBean
    private GoalService goalService;

    // createReview()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createReview_shouldReturn200() throws Exception {

        when(reviewService.createReview(any()))
                .thenReturn(new PerformanceReviewResponse());

        mockMvc.perform(post("/api/performance/reviews")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CreateReviewRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Review created successfully"));
    }

    // provideFeedback()

    @Test
    @WithMockUser(roles = "MANAGER")
    void provideFeedback_shouldReturn200() throws Exception {

        when(reviewService.provideFeedback(eq(1L), any()))
                .thenReturn(new PerformanceReviewResponse());

        mockMvc.perform(put("/api/performance/reviews/1/feedback")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new ProvidedFeedbackRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Feedback provided successfully"));
    }

    // getAllReviews()

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReviews_shouldReturn200() throws Exception {

        when(reviewService.getAllReviews())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/performance/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("All reviews fetched successfully"));
    }

    // deleteReview()

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReview_shouldReturn200() throws Exception {

        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/api/performance/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Review deleted successfully"));
    }

    // createGoal()

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createGoal_shouldReturn200() throws Exception {

        when(goalService.createGoal(any()))
                .thenReturn(new GoalResponse());

        mockMvc.perform(post("/api/performance/goals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CreateGoalRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Goal created successfully"));
    }

    // updateGoalProgress()

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateGoalProgress_shouldReturn200() throws Exception {

        when(goalService.updateGoalProgress(any()))
                .thenReturn(new GoalResponse());

        mockMvc.perform(put("/api/performance/goals/progress")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdateGoalProgressRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Goal progress updated successfully"));
    }

    // getAllGoals()

    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllGoals_shouldReturn200() throws Exception {

        when(goalService.getAllGoals())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/performance/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("All goals fetched successfully"));
    }
}