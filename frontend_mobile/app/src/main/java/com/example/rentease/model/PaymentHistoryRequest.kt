package com.example.rentease.model

data class PaymentHistoryRequest(
    val unitName: String,
    val roomId: Long,
    val rentalFee: Double,
    val startDate: String
) 