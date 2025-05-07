package com.it342_rentease.it342_rentease_project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payment_history_id;

    private String unitName;
    private Long roomId;
    private Double rentalFee;
    private String startDate;

    // Constructors
    public PaymentHistory() {
    }

    public PaymentHistory(String unitName, Long roomId, Double rentalFee, String startDate) {
        this.unitName = unitName;
        this.roomId = roomId;
        this.rentalFee = rentalFee;
        this.startDate = startDate;
    }

    // Getters and Setters
    public Long getPayment_history_id() {
        return payment_history_id;
    }

    public void setPayment_history_id(Long payment_history_id) {
        this.payment_history_id = payment_history_id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(Double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}