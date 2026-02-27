package com.rev.revworkforcep2.mapper.performance;

import com.rev.revworkforcep2.dto.request.performance.CreateGoalRequest;
import com.rev.revworkforcep2.dto.request.performance.CreatePerformanceReviewRequest;
import com.rev.revworkforcep2.dto.request.performance.CreateReviewRequest;
import com.rev.revworkforcep2.dto.response.performance.GoalResponse;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
import com.rev.revworkforcep2.model.Goal;
import com.rev.revworkforcep2.model.PerformanceReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    Goal toGoalEntity(CreateGoalRequest request);

    @Mapping(target = "employeeId", source = "user.id")
    @Mapping(target = "employeeName", expression = "java(goal.getUser().getFirstName() + ' ' + goal.getUser().getLastName())")
    GoalResponse toGoalResponse(Goal goal);

    PerformanceReview toReviewEntity(CreateReviewRequest request);

    @Mapping(target = "improvements", source = "improvements")
    PerformanceReview toReviewEntity(CreatePerformanceReviewRequest request);

    @Mapping(target = "employeeId", source = "user.id")
    @Mapping(target = "employeeName", expression = "java(review.getUser().getFirstName() + ' ' + review.getUser().getLastName())")
    @Mapping(target = "improvementAreas", source = "improvements")
    PerformanceReviewResponse toReviewResponse(PerformanceReview review);

}
