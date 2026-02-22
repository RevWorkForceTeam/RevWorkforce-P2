package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findAll();

}
