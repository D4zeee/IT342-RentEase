package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    // Custom query methods can be added here if needed
}