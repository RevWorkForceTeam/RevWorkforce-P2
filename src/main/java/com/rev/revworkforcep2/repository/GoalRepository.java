package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    @Query("SELECT g FROM Goal g WHERE g.user.manager.id = :managerId")
    List<Goal> findByUserManagerId(@Param("managerId") Long managerId);
}
