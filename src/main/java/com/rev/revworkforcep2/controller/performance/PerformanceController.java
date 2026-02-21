package com.rev.revworkforcep2.controller.performance;

import com.rev.revworkforcep2.dto.request.performance.*;
import com.rev.revworkforcep2.dto.response.performance.GoalResponse;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
import com.rev.revworkforcep2.dto.response.performance.TeamPerformanceSummaryResponse;
import com.rev.revworkforcep2.service.performance.GoalService;
import com.rev.revworkforcep2.service.performance.PerformanceReviewService;
import com.rev.revworkforcep2.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceReviewService reviewService;
    private final GoalService goalService;

    // ================= REVIEW APIs =================

    @PostMapping("/reviews")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> createReview(
            @RequestBody CreateReviewRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Review created successfully",
                        reviewService.createReview(request)
                )
        );
    }

    @PutMapping("/reviews/submit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> submitReview(
            @RequestBody SubmitPerformanceReviewRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Review submitted successfully",
                        reviewService.submitReview(request)
                )
        );
    }

    @PutMapping("/reviews/{id}/feedback")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> provideFeedback(
            @PathVariable Long id,
            @RequestBody ProvidedFeedbackRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Feedback provided successfully",
                        reviewService.provideFeedback(id, request)
                )
        );
    }

    @PostMapping("/reviews/manual")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> createPerformanceReview(
            @RequestBody CreatePerformanceReviewRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Performance review created successfully",
                        reviewService.createPerformanceReview(request)
                )
        );
    }

    @PostMapping("/reviews/team-summary")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<TeamPerformanceSummaryResponse>> getTeamSummary(
            @RequestBody CreateGlobalRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Team performance summary fetched successfully",
                        reviewService.getTeamPerformanceSummary(request)
                )
        );
    }

    @GetMapping("/reviews/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> getReviewById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Review fetched successfully",
                        reviewService.getReviewById(id)
                )
        );
    }

    @GetMapping("/reviews/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<PerformanceReviewResponse>>> getReviewsByEmployee(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Reviews fetched successfully",
                        reviewService.getReviewsByEmployee(employeeId)
                )
        );
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PerformanceReviewResponse>>> getAllReviews() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "All reviews fetched successfully",
                        reviewService.getAllReviews()
                )
        );
    }

    @DeleteMapping("/reviews/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id) {

        reviewService.deleteReview(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Review deleted successfully",
                        null
                )
        );
    }

    // ================= GOAL APIs =================

    // ✅ UPDATED HERE
    @PostMapping("/goals")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @RequestBody CreateGoalRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Goal created successfully",
                        goalService.createGoal(request)
                )
        );
    }

    // ✅ UPDATED HERE
    @PutMapping("/goals/progress")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoalProgress(
            @RequestBody UpdateGoalProgressRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Goal progress updated successfully",
                        goalService.updateGoalProgress(request)
                )
        );
    }

    @GetMapping("/goals/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoalById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "Goal fetched successfully",
                        goalService.getGoalById(id)
                )
        );
    }

    @GetMapping("/goals")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getAllGoals() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "All goals fetched successfully",
                        goalService.getAllGoals()
                )
        );
    }
}

