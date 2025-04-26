package com.it342_rentease.it342_rentease_project.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class PaymentReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "renter_id", nullable = false)
    private Renter renter;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "approval_status")
private String approvalStatus = "pending"; // default pending




    private LocalDate dueDate;
    private Double rentalFee;
    private String note;

    // Getters and Setters
    public Long getReminderId() {
        return reminderId;
    }

    public void setReminderId(Long reminderId) {
        this.reminderId = reminderId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(Double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public String getApprovalStatus() {
        return approvalStatus;
    }

}
