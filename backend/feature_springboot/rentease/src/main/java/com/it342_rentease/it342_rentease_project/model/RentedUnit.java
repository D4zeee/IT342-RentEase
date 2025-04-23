package com.it342_rentease.it342_rentease_project.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class RentedUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentedUnitId;

    @ManyToOne
    @JoinColumn(name = "renter_id", nullable = false)
    private Renter renter;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private LocalDate startDate;
    private LocalDate endDate;

    public Long getRentedUnitId() {
        return rentedUnitId;
    }

    public void setRentedUnitId(Long rentedUnitId) {
        this.rentedUnitId = rentedUnitId;
    }

    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
