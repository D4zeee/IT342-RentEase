package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByUsername(String username);
}
