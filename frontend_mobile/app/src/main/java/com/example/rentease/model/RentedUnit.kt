package com.example.rentease.model

data class RentedUnit(
    val rentedUnitId: Long,
    val renterId: Long,
    val roomId: Long,
    val startDate: String,
    val endDate: String
)