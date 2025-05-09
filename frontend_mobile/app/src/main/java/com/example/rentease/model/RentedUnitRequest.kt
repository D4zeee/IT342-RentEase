package com.example.rentease.model

data class RentedUnitRequest(
    val renter: SimpleId,
    val room: SimpleId,
    val startDate: String,
    val endDate: String
)

data class SimpleId(
    val renterId: Long? = null,
    val roomId: Long? = null
)