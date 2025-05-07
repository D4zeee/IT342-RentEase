package com.example.rentease.model

data class RentedUnit(
    val rentedUnitId: Long,
    val renterId: Long,
    val roomId: Long,
    val startDate: String,
    val endDate: String,
    val unitName: String
)

data class RentedUnitNotificationDTO(
    val room_id: Long,
    val unitname: String,
    val note: String,
    val approval_status: String,
    val startDate: String
)