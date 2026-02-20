package com.rev.revworkforcep2.service.performance;

import com.rev.revworkforcep2.dto.request.performance.CreateGoalRequest;
import com.rev.revworkforcep2.dto.response.performance.GoalResponse;

import java.util.List;

public interface GoalService {
    GoalResponse createGoal(CreateGoalRequest request);

    GoalResponse getGoalById(Long id);

    List<GoalResponse> getAllGoalsByEmployee(Long employeeId);

    void deleteGoal(Long id);
    GoalResponse updateGoalProgress(Long goalId, Integer progress);
    List<GoalResponse> getAllGoals();


}
