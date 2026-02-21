package com.rev.revworkforcep2.service.performance.impl;

import com.rev.revworkforcep2.dto.request.performance.*;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
import com.rev.revworkforcep2.dto.response.performance.TeamPerformanceSummaryResponse;
import com.rev.revworkforcep2.exception.InvalidRequestException;
import com.rev.revworkforcep2.exception.InvalidStateException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.performance.PerformanceMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.service.performance.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PerformanceMapper performanceMapper;

    @Override
    public PerformanceReviewResponse createReview(CreateReviewRequest request) {

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        PerformanceReview review = performanceMapper.toReviewEntity(request);
        review.setUser(employee);
        review.setStatus(ReviewStatus.DRAFT);

        PerformanceReview savedReview = reviewRepository.save(review);

        return performanceMapper.toReviewResponse(savedReview);
    }




    @Override
    public PerformanceReviewResponse getReviewById(Long id) {
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        return performanceMapper.toReviewResponse(review);
    }

    @Override
    public List<PerformanceReviewResponse> getReviewsByEmployee(Long employeeId) {

        userRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        return reviewRepository.findByUserId(employeeId)

                .stream()
                .map(performanceMapper::toReviewResponse)
                .toList();
    }

    @Override
    public List<PerformanceReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(performanceMapper::toReviewResponse)
                .toList();
    }

    @Override
    public void deleteReview(Long id) {
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        reviewRepository.delete(review);
    }
    @Override
    public PerformanceReviewResponse submitReview(Long reviewId) {

        PerformanceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        if (review.getStatus() != ReviewStatus.DRAFT) {
            throw new IllegalStateException("Only draft reviews can be submitted");
        }

        review.setStatus(ReviewStatus.SUBMITTED);

        PerformanceReview savedReview = reviewRepository.save(review);

        return performanceMapper.toReviewResponse(savedReview);
    }
    @Override
    public PerformanceReviewResponse provideFeedback(Long reviewId, String feedback, Integer rating) {

        PerformanceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        if (review.getStatus() != ReviewStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted reviews can be reviewed");
        }

        review.setManagerFeedback(feedback);
        review.setManagerRating(rating);
        review.setStatus(ReviewStatus.REVIEWED);

        PerformanceReview savedReview = reviewRepository.save(review);

        return performanceMapper.toReviewResponse(savedReview);
    }
    @Override
    public TeamPerformanceSummaryResponse getTeamPerformanceSummary(
            CreateGlobalRequest request) {

        if (request.getManagerId() == null) {
            throw new InvalidRequestException("Manager ID is required");
        }

        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Manager not found"));

        // Get team members
        List<User> teamMembers =
                userRepository.findByManagerId(manager.getId());

        if (teamMembers.isEmpty()) {
            throw new ResourceNotFoundException("No team members found");
        }

        // Get all reviews of team members
        List<PerformanceReview> reviews = reviewRepository.findAll()
                .stream()
                .filter(r -> teamMembers.contains(r.getUser()))
                .toList();

        // Filter by year (if provided)
        if (request.getYear() != null) {
            reviews = reviews.stream()
                    .filter(r -> r.getYear() == request.getYear())
                    .toList();
        }

        // Filter by status (if provided)
        if (request.getStatus() != null) {
            ReviewStatus status;
            try {
                status = ReviewStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid review status");
            }

            reviews = reviews.stream()
                    .filter(r -> r.getStatus() == status)
                    .toList();
        }

        int totalReviews = reviews.size();

        int completedReviews = (int) reviews.stream()
                .filter(r -> r.getStatus() == ReviewStatus.REVIEWED)
                .count();

        double averageRating = reviews.stream()
                .filter(r -> r.getManagerRating() > 0)
                .mapToInt(PerformanceReview::getManagerRating)
                .average()
                .orElse(0.0);

        TeamPerformanceSummaryResponse response =
                new TeamPerformanceSummaryResponse();

        response.setManagerId(manager.getId());
        response.setTotalReviews(totalReviews);
        response.setCompletedReviews(completedReviews);
        response.setAverageRating(averageRating);
        response.setReviews(
                reviews.stream()
                        .map(performanceMapper::toReviewResponse)
                        .toList()
        );

        return response;
    }
    @Override
    public PerformanceReviewResponse createPerformanceReview(
            CreatePerformanceReviewRequest request) {

        if (request.getEmployeeId() == null) {
            throw new InvalidRequestException("Employee ID is required");
        }

        if (request.getYear() == null) {
            throw new InvalidRequestException("Year is required");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        // Prevent duplicate review for same year
        if (reviewRepository
                .findByUserIdAndYear(employee.getId(), request.getYear())
                .isPresent()) {

            throw new InvalidRequestException(
                    "Performance review already exists for year "
                            + request.getYear());
        }

        // Validate rating range (1–5)
        if (request.getSelfRating() == null ||
                request.getSelfRating() < 1 ||
                request.getSelfRating() > 5) {

            throw new InvalidRequestException(
                    "Self rating must be between 1 and 5");
        }

        // Map DTO → Entity
        PerformanceReview review =
                performanceMapper.toReviewEntity(request);

        review.setUser(employee);
        review.setManagerRating(0);
        review.setStatus(ReviewStatus.DRAFT);

        PerformanceReview saved =
                reviewRepository.save(review);

        return performanceMapper.toReviewResponse(saved);
    }
    @Override
    public PerformanceReviewResponse provideFeedback(
            Long reviewId,
            ProvidedFeedbackRequest request) {

        PerformanceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        // Only submitted reviews can be reviewed
        if (review.getStatus() != ReviewStatus.SUBMITTED) {
            throw new InvalidStateException(
                    "Only submitted reviews can be reviewed");
        }

        // Validate rating
        if (request.getRating() == null ||
                request.getRating() < 1 ||
                request.getRating() > 5) {

            throw new InvalidRequestException(
                    "Rating must be between 1 and 5");
        }

        review.setManagerFeedback(request.getFeedback());
        review.setManagerRating(request.getRating());
        review.setStatus(ReviewStatus.REVIEWED);

        PerformanceReview saved = reviewRepository.save(review);

        return performanceMapper.toReviewResponse(saved);
    }
    @Override
    public PerformanceReviewResponse submitReview(
            SubmitPerformanceReviewRequest request) {

        if (request.getReviewId() == null) {
            throw new InvalidRequestException("Review ID is required");
        }

        PerformanceReview review = reviewRepository
                .findById(request.getReviewId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        // Only DRAFT reviews can be submitted
        if (review.getStatus() != ReviewStatus.DRAFT) {
            throw new InvalidStateException(
                    "Only draft reviews can be submitted");
        }

        review.setStatus(ReviewStatus.SUBMITTED);

        PerformanceReview savedReview = reviewRepository.save(review);

        return performanceMapper.toReviewResponse(savedReview);
    }


}
