package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.Renter;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RenterRepository extends JpaRepository<Renter, Long> {
    boolean existsByEmail(String email);
    Optional<Renter> findByEmail(String email);

}
