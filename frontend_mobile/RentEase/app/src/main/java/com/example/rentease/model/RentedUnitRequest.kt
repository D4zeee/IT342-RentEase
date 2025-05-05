package com.example.rentease.model

data class RentedUnitRequest(
    val renter: RenterRef,
    val room: RoomRef,
    val startDate: String,
    val endDate: String
)

data class RenterRef(val renterId: Long)
data class RoomRef(val roomId: Long)