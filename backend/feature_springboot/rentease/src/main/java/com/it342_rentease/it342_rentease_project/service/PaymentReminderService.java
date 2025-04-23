package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.PaymentReminder;
import com.it342_rentease.it342_rentease_project.repository.PaymentReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentReminderService {

    @Autowired
    private PaymentReminderRepository paymentReminderRepository;

    public PaymentReminder create(PaymentReminder reminder) {
        return paymentReminderRepository.save(reminder);
    }

    public List<PaymentReminder> getAll() {
        return paymentReminderRepository.findAll();
    }

    public Optional<PaymentReminder> getById(Long id) {
        return paymentReminderRepository.findById(id);
    }

    public List<PaymentReminder> getByRenterId(Long renterId) {
        return paymentReminderRepository.findByRenterRenterId(renterId);
    }

    public List<PaymentReminder> getByOwnerId(Long ownerId) {
        return paymentReminderRepository.findByOwnerOwnerId(ownerId);
    }

    public List<PaymentReminder> getByRoomId(Long roomId) {
        return paymentReminderRepository.findByRoomRoomId(roomId);
    }

    public void delete(Long id) {
        paymentReminderRepository.deleteById(id);
    }
}
