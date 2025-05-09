package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.PaymentHistory;
import com.it342_rentease.it342_rentease_project.service.PaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-history")
@CrossOrigin(origins = "*") // Allow CORS for web/mobile
public class PaymentHistoryController {

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @PostMapping
    public PaymentHistory createPaymentHistory(@RequestBody PaymentHistory paymentHistory) {
        return paymentHistoryService.savePaymentHistory(paymentHistory);
    }

    @GetMapping
    public List<PaymentHistory> getAllPaymentHistories() {
        return paymentHistoryService.getAllPaymentHistories();
    }

    @GetMapping("/{id}")
    public PaymentHistory getPaymentHistoryById(@PathVariable Long id) {
        return paymentHistoryService.getPaymentHistoryById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePaymentHistory(@PathVariable Long id) {
        paymentHistoryService.deletePaymentHistory(id);
    }
}