package com.rev.revworkforcep2.service.performance;

import com.rev.revworkforcep2.dto.request.performance.CreateGoalRequest;
import com.rev.revworkforcep2.dto.request.performance.UpdateGoalProgressRequest;
import com.rev.revworkforcep2.dto.response.performance.GoalResponse;
import com.rev.revworkforcep2.exception.InvalidRequestException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.performance.PerformanceMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.GoalRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.performance.impl.GoalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock private GoalRepository goalRepository;
    @Mock private UserRepository userRepository;
    @Mock private PerformanceMapper performanceMapper;

    @InjectMocks
    private GoalServiceImpl service;

    // CREATE SUCCESS
    @Test
    void createGoal_shouldCreateSuccessfully() {

        CreateGoalRequest request = new CreateGoalRequest();
        request.setEmployeeId(1L);

        User employee = new User();
        Goal goal = new Goal();
        Goal saved = new Goal();
        GoalResponse response = new GoalResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(performanceMapper.toGoalEntity(request)).thenReturn(goal);
        when(goalRepository.save(goal)).thenReturn(saved);
        when(performanceMapper.toGoalResponse(saved)).thenReturn(response);

        GoalResponse result = service.createGoal(request);

        assertThat(goal.getUser()).isEqualTo(employee);
        assertThat(goal.getStatus()).isEqualTo(GoalStatus.NOT_STARTED);
        assertThat(result).isEqualTo(response);
    }

    // CREATE EMPLOYEE NOT FOUND
    @Test
    void createGoal_shouldThrowException_whenEmployeeNotFound() {

        CreateGoalRequest request = new CreateGoalRequest();
        request.setEmployeeId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createGoal(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found");
    }

    // GET BY ID SUCCESS
    @Test
    void getGoalById_shouldReturnGoal() {

        Goal goal = new Goal();
        GoalResponse response = new GoalResponse();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(performanceMapper.toGoalResponse(goal)).thenReturn(response);

        GoalResponse result = service.getGoalById(1L);

        assertThat(result).isEqualTo(response);
    }

    // GET BY ID NOT FOUND
    @Test
    void getGoalById_shouldThrowException_whenNotFound() {

        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getGoalById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal not found");
    }

    // GET ALL BY EMPLOYEE SUCCESS
    @Test
    void getAllGoalsByEmployee_shouldReturnList() {

        User user = new User();
        Goal goal = new Goal();
        GoalResponse response = new GoalResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.findByUserId(1L)).thenReturn(List.of(goal));
        when(performanceMapper.toGoalResponse(goal)).thenReturn(response);

        List<GoalResponse> result = service.getAllGoalsByEmployee(1L);

        assertThat(result).hasSize(1);
    }

    // GET ALL BY EMPLOYEE NOT FOUND
    @Test
    void getAllGoalsByEmployee_shouldThrowException_whenEmployeeNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllGoalsByEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found");
    }

    // GET ALL GOALS
    @Test
    void getAllGoals_shouldReturnList() {

        Goal goal = new Goal();
        GoalResponse response = new GoalResponse();

        when(goalRepository.findAll()).thenReturn(List.of(goal));
        when(performanceMapper.toGoalResponse(goal)).thenReturn(response);

        List<GoalResponse> result = service.getAllGoals();

        assertThat(result).hasSize(1);
    }

    // DELETE SUCCESS
    @Test
    void deleteGoal_shouldDeleteSuccessfully() {

        Goal goal = new Goal();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        service.deleteGoal(1L);

        verify(goalRepository).delete(goal);
    }

    // DELETE NOT FOUND
    @Test
    void deleteGoal_shouldThrowException_whenNotFound() {

        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteGoal(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal not found");
    }

    // UPDATE PROGRESS SUCCESS - NOT_STARTED
    @Test
    void updateGoalProgress_shouldSetNotStarted() {

        UpdateGoalProgressRequest request = new UpdateGoalProgressRequest();
        request.setGoalId(1L);
        request.setProgress(0);

        Goal goal = new Goal();
        Goal saved = new Goal();
        GoalResponse response = new GoalResponse();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(saved);
        when(performanceMapper.toGoalResponse(saved)).thenReturn(response);

        GoalResponse result = service.updateGoalProgress(request);

        assertThat(goal.getStatus()).isEqualTo(GoalStatus.NOT_STARTED);
        assertThat(result).isEqualTo(response);
    }

    // UPDATE PROGRESS SUCCESS - IN_PROGRESS
    @Test
    void updateGoalProgress_shouldSetInProgress() {

        UpdateGoalProgressRequest request = new UpdateGoalProgressRequest();
        request.setGoalId(1L);
        request.setProgress(50);

        Goal goal = new Goal();
        Goal saved = new Goal();
        GoalResponse response = new GoalResponse();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(saved);
        when(performanceMapper.toGoalResponse(saved)).thenReturn(response);

        service.updateGoalProgress(request);

        assertThat(goal.getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
    }

    // UPDATE PROGRESS SUCCESS - COMPLETED
    @Test
    void updateGoalProgress_shouldSetCompleted() {

        UpdateGoalProgressRequest request = new UpdateGoalProgressRequest();
        request.setGoalId(1L);
        request.setProgress(100);

        Goal goal = new Goal();
        Goal saved = new Goal();
        GoalResponse response = new GoalResponse();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(saved);
        when(performanceMapper.toGoalResponse(saved)).thenReturn(response);

        service.updateGoalProgress(request);

        assertThat(goal.getStatus()).isEqualTo(GoalStatus.COMPLETED);
    }

    // UPDATE PROGRESS INVALID RANGE
    @Test
    void updateGoalProgress_shouldThrowException_whenInvalidProgress() {

        UpdateGoalProgressRequest request = new UpdateGoalProgressRequest();
        request.setGoalId(1L);
        request.setProgress(150);

        assertThatThrownBy(() -> service.updateGoalProgress(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Progress must be between 0 and 100");
    }

    // UPDATE PROGRESS NULL ID
    @Test
    void updateGoalProgress_shouldThrowException_whenGoalIdNull() {

        UpdateGoalProgressRequest request = new UpdateGoalProgressRequest();
        request.setGoalId(null);
        request.setProgress(50);

        assertThatThrownBy(() -> service.updateGoalProgress(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Goal ID is required");
    }
}