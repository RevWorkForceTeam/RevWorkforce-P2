package com.rev.revworkforcep2.service.performance.impl;

import com.rev.revworkforcep2.dto.request.performance.CreateGoalRequest;
import com.rev.revworkforcep2.dto.request.performance.UpdateGoalProgressRequest;
import com.rev.revworkforcep2.dto.response.performance.GoalResponse;
import com.rev.revworkforcep2.exception.InvalidRequestException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.performance.PerformanceMapper;
import com.rev.revworkforcep2.model.Goal;
import com.rev.revworkforcep2.model.GoalStatus;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.GoalRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.performance.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final PerformanceMapper performanceMapper;

    // ================= CREATE GOAL =================
    @Override
    public GoalResponse createGoal(CreateGoalRequest request) {

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        Goal goal = performanceMapper.toGoalEntity(request);

        goal.setUser(employee);
        goal.setStatus(GoalStatus.NOT_STARTED);

        Goal savedGoal = goalRepository.save(goal);

        return performanceMapper.toGoalResponse(savedGoal);
    }

    // ================= GET BY ID =================
    @Override
    public GoalResponse getGoalById(Long id) {

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        return performanceMapper.toGoalResponse(goal);
    }

    // ================= GET ALL BY EMPLOYEE =================
    @Override
    public List<GoalResponse> getAllGoalsByEmployee(Long employeeId) {

        userRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        return goalRepository.findByUserId(employeeId)
                .stream()
                .map(performanceMapper::toGoalResponse)
                .toList();
    }

    // ================= GET ALL GOALS =================
    @Override
    public List<GoalResponse> getAllGoals() {

        return goalRepository.findAll()
                .stream()
                .map(performanceMapper::toGoalResponse)
                .toList();
    }

    // ================= DELETE GOAL =================
    @Override
    public void deleteGoal(Long id) {

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        goalRepository.delete(goal);
    }

    // ================= UPDATE PROGRESS =================
//    @Override
//    public GoalResponse updateGoalProgress(Long goalId, Integer progress) {
//
//        Goal goal = goalRepository.findById(goalId)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Goal not found"));
//
//        goal.setProgress(progress);
//
//        if (progress != null && progress == 100) {
//            goal.setStatus(GoalStatus.COMPLETED);
//        } else {
//            goal.setStatus(GoalStatus.IN_PROGRESS);
//        }
//
//        Goal savedGoal = goalRepository.save(goal);
//
//        return performanceMapper.toGoalResponse(savedGoal);
//    }
    @Override
    public GoalResponse updateGoalProgress(
            UpdateGoalProgressRequest request) {

        if (request.getGoalId() == null) {
            throw new InvalidRequestException("Goal ID is required");
        }

        if (request.getProgress() == null ||
                request.getProgress() < 0 ||
                request.getProgress() > 100) {

            throw new InvalidRequestException(
                    "Progress must be between 0 and 100");
        }

        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        goal.setProgress(request.getProgress());

        // Update status based on progress
        if (request.getProgress() == 0) {
            goal.setStatus(GoalStatus.NOT_STARTED);
        } else if (request.getProgress() < 100) {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        } else {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        Goal savedGoal = goalRepository.save(goal);

        return performanceMapper.toGoalResponse(savedGoal);
    }
}
