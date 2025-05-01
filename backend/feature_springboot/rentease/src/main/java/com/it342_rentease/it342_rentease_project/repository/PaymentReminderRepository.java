package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.PaymentReminder;
import com.it342_rentease.it342_rentease_project.model.Room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentReminderRepository extends JpaRepository<PaymentReminder, Long> {
    List<PaymentReminder> findByRenterRenterId(Long renterId);
    List<PaymentReminder> findByOwnerOwnerId(Long ownerId);
    List<PaymentReminder> findByRoomRoomId(Long roomId);
     List<PaymentReminder> findByRoom(Room room); 
}
