package com.it342_rentease.it342_rentease_project.model;

import jakarta.persistence.*;
import com.it342_rentease.it342_rentease_project.model.Room;


import java.time.LocalDate;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    private float amount;
    private String status;
    private String paymentMethod;
    
    @ManyToOne
@JoinColumn(name = "room_id", nullable = false)
private Room room;

    public Payment() {
    }

    public Payment(int bookingId, float amount, String status, String paymentMethod) {
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public int getPaymentId() {
        return paymentId;
    }


    public void setPaymentId(int paymentId) {
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

    public Room getRoom() {
        return room;
    }
    
    public void setRoom(Room room) {
        this.room = room;
    }
    
}