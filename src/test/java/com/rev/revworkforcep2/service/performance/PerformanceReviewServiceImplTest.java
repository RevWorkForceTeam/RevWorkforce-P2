package com.rev.revworkforcep2.service.performance;

import com.rev.revworkforcep2.dto.request.performance.*;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
import com.rev.revworkforcep2.dto.response.performance.TeamPerformanceSummaryResponse;
import com.rev.revworkforcep2.exception.InvalidRequestException;
import com.rev.revworkforcep2.exception.InvalidStateException;
import com.rev.revworkforcep2.mapper.performance.PerformanceMapper;
import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import com.rev.revworkforcep2.service.performance.impl.PerformanceReviewServiceImpl;
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
class PerformanceReviewServiceImplTest {

    @Mock private PerformanceReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private PerformanceMapper performanceMapper;

    @InjectMocks
    private PerformanceReviewServiceImpl service;

    // CREATE REVIEW SUCCESS
    @Test
    void createReview_shouldCreateSuccessfully() {

        CreateReviewRequest request = new CreateReviewRequest();
        request.setEmployeeId(1L);

        User user = new User();
        PerformanceReview review = new PerformanceReview();
        PerformanceReview saved = new PerformanceReview();
        PerformanceReviewResponse response = new PerformanceReviewResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(performanceMapper.toReviewEntity(request)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(saved);
        when(performanceMapper.toReviewResponse(saved)).thenReturn(response);

        PerformanceReviewResponse result = service.createReview(request);

        assertThat(review.getStatus()).isEqualTo(ReviewStatus.DRAFT);
        assertThat(result).isEqualTo(response);
    }

    // SUBMIT REVIEW SUCCESS (DTO VERSION)
    @Test
    void submitReview_shouldSubmitSuccessfully() {

        SubmitPerformanceReviewRequest request =
                new SubmitPerformanceReviewRequest();
        request.setReviewId(1L);

        PerformanceReview review = new PerformanceReview();
        review.setStatus(ReviewStatus.DRAFT);

        PerformanceReview saved = new PerformanceReview();
        PerformanceReviewResponse response =
                new PerformanceReviewResponse();

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));
        when(reviewRepository.save(review))
                .thenReturn(saved);
        when(performanceMapper.toReviewResponse(saved))
                .thenReturn(response);

        PerformanceReviewResponse result =
                service.submitReview(request);

        assertThat(review.getStatus())
                .isEqualTo(ReviewStatus.SUBMITTED);
        assertThat(result).isEqualTo(response);
    }

    // SUBMIT REVIEW INVALID STATE
    @Test
    void submitReview_shouldThrowException_whenNotDraft() {

        SubmitPerformanceReviewRequest request =
                new SubmitPerformanceReviewRequest();
        request.setReviewId(1L);

        PerformanceReview review = new PerformanceReview();
        review.setStatus(ReviewStatus.SUBMITTED);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        assertThatThrownBy(() -> service.submitReview(request))
                .isInstanceOf(InvalidStateException.class)
                .hasMessage("Only draft reviews can be submitted");
    }

    // PROVIDE FEEDBACK SUCCESS
    @Test
    void provideFeedback_shouldReviewSuccessfully() {

        ProvidedFeedbackRequest request =
                new ProvidedFeedbackRequest();
        request.setFeedback("Good job");
        request.setRating(4);

        PerformanceReview review = new PerformanceReview();
        review.setStatus(ReviewStatus.SUBMITTED);

        PerformanceReview saved = new PerformanceReview();
        PerformanceReviewResponse response =
                new PerformanceReviewResponse();

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));
        when(reviewRepository.save(review))
                .thenReturn(saved);
        when(performanceMapper.toReviewResponse(saved))
                .thenReturn(response);

        PerformanceReviewResponse result =
                service.provideFeedback(1L, request);

        assertThat(review.getStatus())
                .isEqualTo(ReviewStatus.REVIEWED);
        assertThat(result).isEqualTo(response);
    }

    // PROVIDE FEEDBACK INVALID RATING
    @Test
    void provideFeedback_shouldThrowException_whenRatingInvalid() {

        ProvidedFeedbackRequest request =
                new ProvidedFeedbackRequest();
        request.setRating(6);

        PerformanceReview review = new PerformanceReview();
        review.setStatus(ReviewStatus.SUBMITTED);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        assertThatThrownBy(() ->
                service.provideFeedback(1L, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Rating must be between 1 and 5");
    }

    // CREATE PERFORMANCE REVIEW DUPLICATE
    @Test
    void createPerformanceReview_shouldThrowException_whenDuplicateYear() {

        CreatePerformanceReviewRequest request =
                new CreatePerformanceReviewRequest();
        request.setEmployeeId(1L);
        request.setYear(2025);
        request.setSelfRating(3);

        User user = new User();
        user.setId(1L);

        PerformanceReview existing = new PerformanceReview();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(reviewRepository
                .findByUserIdAndYear(1L, 2025))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() ->
                service.createPerformanceReview(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Performance review already exists");
    }

    // TEAM SUMMARY SUCCESS
    @Test
    void getTeamPerformanceSummary_shouldReturnSummary() {

        CreateGlobalRequest request =
                new CreateGlobalRequest();
        request.setManagerId(1L);

        User manager = new User();
        manager.setId(1L);

        User teamMember = new User();
        teamMember.setId(2L);

        PerformanceReview review = new PerformanceReview();
        review.setUser(teamMember);
        review.setStatus(ReviewStatus.REVIEWED);
        review.setManagerRating(4);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(1L))
                .thenReturn(List.of(teamMember));
        when(reviewRepository.findAll())
                .thenReturn(List.of(review));
        when(performanceMapper.toReviewResponse(review))
                .thenReturn(new PerformanceReviewResponse());

        TeamPerformanceSummaryResponse result =
                service.getTeamPerformanceSummary(request);

        assertThat(result.getTotalReviews()).isEqualTo(1);
        assertThat(result.getCompletedReviews()).isEqualTo(1);
        assertThat(result.getAverageRating()).isEqualTo(4.0);
    }

    // TEAM SUMMARY INVALID STATUS
    @Test
    void getTeamPerformanceSummary_shouldThrowException_whenInvalidStatus() {

        CreateGlobalRequest request =
                new CreateGlobalRequest();
        request.setManagerId(1L);
        request.setStatus("INVALID");

        User manager = new User();
        manager.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(1L))
                .thenReturn(List.of(new User()));
        when(reviewRepository.findAll())
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                service.getTeamPerformanceSummary(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Invalid review status");
    }
}