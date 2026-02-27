package com.rev.revworkforcep2.service.performance.impl;

import com.rev.revworkforcep2.dto.request.performance.AddGoalCommentRequest;
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
import com.rev.revworkforcep2.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final PerformanceMapper performanceMapper;
    private final NotificationService notificationService;

    @Override
    public GoalResponse createGoal(CreateGoalRequest request) {

        Long employeeId = request.getEmployeeId();
        if (employeeId == null) {
            employeeId = com.rev.revworkforcep2.security.util.SecurityUtils.getCurrentUserId();
        }

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        Goal goal = performanceMapper.toGoalEntity(request);

        goal.setUser(employee);
        goal.setStatus(GoalStatus.NOT_STARTED);

        Goal savedGoal = goalRepository.save(goal);

        if (employee.getManager() != null) {
            notificationService.triggerForUser(
                    employee.getManager().getId(),
                    "<strong>" + employee.getFirstName() + " " + employee.getLastName() + "</strong> created a new goal: " + goal.getTitle(),
                    "GOAL"
            );
        } else {
            List<User> admins = userRepository.findAll().stream()
                    .filter(u -> com.rev.revworkforcep2.model.Role.ADMIN.equals(u.getRole()))
                    .toList();
            if (!admins.isEmpty()) {
                notificationService.triggerForUser(
                        admins.get(0).getId(),
                        "<strong>" + employee.getFirstName() + " " + employee.getLastName() + "</strong> created a new goal: " + goal.getTitle(),
                        "GOAL"
                );
            }
        }

        return performanceMapper.toGoalResponse(savedGoal);
    }

    @Override
    public GoalResponse getGoalById(Long id) {

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        return performanceMapper.toGoalResponse(goal);
    }

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

    @Override
    public List<GoalResponse> getAllGoals() {

        return goalRepository.findAll()
                .stream()
                .map(performanceMapper::toGoalResponse)
                .toList();
    }

    @Override
    public List<GoalResponse> getMyGoals() {
        Long userId = com.rev.revworkforcep2.security.util.SecurityUtils.getCurrentUserId();
        return goalRepository.findByUserId(userId)
                .stream()
                .map(performanceMapper::toGoalResponse)
                .toList();
    }

    @Override
    public List<GoalResponse> getTeamGoals() {
        Long managerId = com.rev.revworkforcep2.security.util.SecurityUtils.getCurrentUserId();
        return goalRepository.findByUserManagerId(managerId)
                .stream()
                .map(performanceMapper::toGoalResponse)
                .toList();
    }

    @Override
    public GoalResponse addManagerComment(AddGoalCommentRequest request) {
        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goal.setManagerComment(request.getComment());
        Goal savedGoal = goalRepository.save(goal);


        String message = String.format(
                "Your manager has reviewed your goal: <strong>%s</strong>. <span style='color:#10b981;'>Comment: %s</span>",
                goal.getTitle(),
                request.getComment()
        );
        notificationService.triggerForUser(goal.getUser().getId(), message, "GOAL_COMMENT");

        return performanceMapper.toGoalResponse(savedGoal);
    }

    @Override
    public void deleteGoal(Long id) {

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        goalRepository.delete(goal);
    }

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
