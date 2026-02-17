package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
}
