package com.revworkforcep2.service.performance;

import com.rev.revworkforcep2.dto.request.performance.CreateReviewRequest;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
import com.rev.revworkforcep2.model.PerformanceReview;
import com.rev.revworkforcep2.repository.PerformanceReviewRepository;
import com.rev.revworkforcep2.service.performance.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.revworkforcep2.repository.*;
import com.revworkforce.dto.performance.*;
import com.revworkforce.entity.*;
import com.revworkforce.entity.enums.*;
import com.revworkforce.mapper.performance.PerformanceMapper;
import com.revworkforce.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final PerformanceMapper performanceMapper;

    @Override
    public PerformanceReviewResponse createReview(CreateReviewRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        PerformanceReview review = performanceMapper.toReview(request);

        review.setEmployee(employee);
        review.setStatus(ReviewStatus.DRAFT);
        review.setManagerFeedback(null);
        review.setManagerRating(null);

        PerformanceReview saved = reviewRepository.save(review);

        return performanceMapper.toReviewResponse(saved);
    }

    @Override
    public PerformanceReviewResponse getReviewById(Long id) {

        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        return performanceMapper.toReviewResponse(review);
    }

    @Override
    public List<PerformanceReviewResponse> getReviewsByEmployee(Long employeeId) {

        List<PerformanceReview> reviews = reviewRepository.findByEmployeeId(employeeId);

        return reviews.stream()
                .map(performanceMapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(Long id) {

        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        reviewRepository.delete(review);
    }
}
