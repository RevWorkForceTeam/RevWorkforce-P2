package com.rev.revworkforcep2.service.performance.impl;

import com.rev.revworkforcep2.dto.request.performance.*;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
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


}
