package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.Payment;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.PaymentRepository;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
private RoomRepository roomRepository;

    
public Payment createPayment(Payment payment) {
    Long roomId = payment.getRoom().getRoomId();

    Room existingRoom = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));

    payment.setRoom(existingRoom); // Populate full Room info
    return paymentRepository.save(payment);
}

    public List<Payment> getPaymentsByRoomId(Long roomId) {
        return paymentRepository.findByRoomRoomId(roomId);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public boolean deletePayment(Long id) {
        try {
            paymentRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
