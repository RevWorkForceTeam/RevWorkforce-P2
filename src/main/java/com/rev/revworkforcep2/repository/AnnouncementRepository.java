package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
