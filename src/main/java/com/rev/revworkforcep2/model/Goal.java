package com.rev.revworkforcep2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private Integer progress;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

