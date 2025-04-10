package com.it342_rentease.it342_rentease_project.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "checkins")
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkin_id")
    private Long checkinId;

    @ManyToOne
    @JoinColumn(name = "renter_id", nullable = false)
    private Renter renter;

    // Renamed to "id" but still maps to column "room_id"
    @Column(name = "room_id", nullable = false)
    private Long id;

    private LocalDate checkinDate;
    private LocalDate checkoutDate;

    // Getters and Setters

    public Long getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(Long checkinId) {
        this.checkinId = checkinId;
    }

    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    public Long getId() { // <- was getRoomId()
        return id;
    }

    public void setId(Long id) { // <- was setRoomId()
        this.id = id;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }
}
