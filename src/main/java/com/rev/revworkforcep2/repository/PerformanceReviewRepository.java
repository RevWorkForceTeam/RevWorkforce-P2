package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {

    List<PerformanceReview> findByUserId(Long userId);

    Optional<PerformanceReview> findByUserIdAndYear(Long userId, int year);
}
