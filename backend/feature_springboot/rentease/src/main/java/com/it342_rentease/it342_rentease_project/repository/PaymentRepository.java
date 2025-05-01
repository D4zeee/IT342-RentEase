package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRoomRoomId(Long roomId);
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);
    List<Payment> findByRoomOwnerOwnerIdAndStatus(Long ownerId, String status);
    
}
