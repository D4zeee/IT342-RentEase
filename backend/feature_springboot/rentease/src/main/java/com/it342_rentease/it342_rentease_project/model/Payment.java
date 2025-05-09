package com.it342_rentease.it342_rentease_project.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private float amount;
    private String status;
    private String paymentMethod;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "paid_date")
    private LocalDate paidDate; // Add this field for paid_date

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public Payment() {
    }

    public Payment(float amount, String status, String paymentMethod, String paymentIntentId, LocalDate paidDate,
            Room room) {
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentIntentId = paymentIntentId;
        this.paidDate = paidDate;
        this.room = room;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}