package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    Optional<Designation> findByTitle(String title);

    boolean existsByTitle(String title);
}