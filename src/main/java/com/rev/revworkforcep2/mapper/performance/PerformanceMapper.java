package com.rev.revworkforcep2.mapper.performance;

import com.rev.revworkforcep2.dto.request.performance.CreateGoalRequest;
import com.rev.revworkforcep2.dto.request.performance.CreateReviewRequest;
import com.rev.revworkforcep2.dto.response.performance.GoalResponse;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
import com.rev.revworkforcep2.model.Goal;
import com.rev.revworkforcep2.model.PerformanceReview;
import org.mapstruct.Mapper;

//@Mapper(componentModel = "spring")
@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    Goal toGoalEntity(CreateGoalRequest request);
    GoalResponse toGoalResponse(Goal goal);

    PerformanceReview toReviewEntity(CreateReviewRequest request);  // ADD THIS

    PerformanceReviewResponse toReviewResponse(PerformanceReview review);
}

