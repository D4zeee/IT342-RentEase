package com.example.rentease.model

data class RentedUnit(
    val rentedUnitId: Long? = null,
    val renter: RenterRef,
    val room: RoomRef,
    val startDate: String,
    val endDate: String
)

