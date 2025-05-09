package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.PaymentHistory;
import com.it342_rentease.it342_rentease_project.repository.PaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentHistoryService {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    public PaymentHistory savePaymentHistory(PaymentHistory paymentHistory) {
        return paymentHistoryRepository.save(paymentHistory);
    }

    public List<PaymentHistory> getAllPaymentHistories() {
        return paymentHistoryRepository.findAll();
    }

    public PaymentHistory getPaymentHistoryById(Long id) {
        return paymentHistoryRepository.findById(id).orElse(null);
    }

    public void deletePaymentHistory(Long id) {
        paymentHistoryRepository.deleteById(id);
    }
} 