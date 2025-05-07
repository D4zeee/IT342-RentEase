package com.example.rentease.model

data class PaymentReminderDto(
    val reminderId: Long?,
    val room: RoomDto,
    val dueDate: String?,
    val note: String?
)

data class RoomDto(
    val roomId: Long?,
    val unitName: String?
) 