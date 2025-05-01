package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {
}
