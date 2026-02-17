package com.rev.revworkforcep2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "performance_reviews")
@Getter
@Setter
@NoArgsConstructor
public class PerformanceReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int year;

    private String deliverables;
    private String accomplishments;
    private String improvements;

    private int selfRating;
    private int managerRating;

    private String managerFeedback;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
}
