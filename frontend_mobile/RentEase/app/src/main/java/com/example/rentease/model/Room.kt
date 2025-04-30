package com.example.rentease.model

data class Room(
    val roomId: Long,
    val unitName: String,
    val description: String,
    val rentalFee: Double,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val postalCode: String,
    val status: String,
    val numberOfRooms: Int,
    val imagePaths: List<String> = emptyList(),
    val ownerId: Long?,       // 🔥 ADD THIS
    val ownerName: String?    // 🔥 ADD THIS
)
