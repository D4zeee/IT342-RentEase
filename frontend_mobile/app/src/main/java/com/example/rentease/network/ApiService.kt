package com.example.rentease.network

import com.example.rentease.model.Room
import com.example.rentease.model.RentedUnit
import com.example.rentease.model.RentedUnitRequest
import com.example.rentease.model.RentedUnitNotificationDTO
import com.example.rentease.model.PaymentReminderDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/renters/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("/api/renters/register") // ✅ match backend controller endpoint
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Void> // or whatever your backend returns

    @GET("/api/renters/current") // 🔥 correct it here
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

    @PATCH("/api/renters/update-name")
    suspend fun updateRenterName(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Map<String, Any>>

    @DELETE("/api/renters/delete")
    suspend fun deleteRenter(@Header("Authorization") token: String
    ): Response<Map<String, Any>>

    @GET("/rooms/{roomId}")
    suspend fun getRoomById(
        @Header("Authorization") token: String,
        @Path("roomId") roomId: Long
    ): Response<Room>

    @GET("/rented_units")
    suspend fun getAllRentedUnits(
        @Header("Authorization") token: String
    ): Response<List<RentedUnit>>

    @GET("rooms")
    suspend fun getAllRooms(
        @Header("Authorization") token: String
    ): Response<List<Room>>

    @PATCH("rooms/{roomId}/status")
    suspend fun updateRoomStatus(
        @Path("roomId") roomId: Long,
        @Body status: Map<String, String>,
        @Header("Authorization") authHeader: String
    ): Response<Void>


    @GET("/rented_units/renter/{renterId}/rooms")
    suspend fun getRenterRooms(
        @Header("Authorization") token: String,
        @Path("renterId") renterId: Long
    ): Response<List<Room>>

    @POST("/rented_units")
    suspend fun bookRoom(
        @Header("Authorization") token: String,
        @Body request: RentedUnitRequest
    ): Response<Map<String, Any>> // ✅ Fix

    @GET("/rented_units/renter/{renterId}")
    suspend fun getRentedUnitsByRenter(
        @Header("Authorization") token: String,
        @Path("renterId") renterId: Long
    ): Response<List<RentedUnit>>

    @GET("/rented_units/renter/{renterId}/notifications")
    suspend fun getRentedUnitNotificationsByRenter(
        @Header("Authorization") token: String,
        @Path("renterId") renterId: Long
    ): Response<List<RentedUnitNotificationDTO>>

    @GET("rented_units/renter/{renterId}/rooms")
    suspend fun getBookedOrRentedRoomsForRenter(
        @Header("Authorization") token: String,
        @Path("renterId") renterId: Long
    ): Response<List<Room>>

    @GET("/payment_reminders/renter/{renterId}")
    suspend fun getRemindersByRenter(
        @Header("Authorization") token: String,
        @Path("renterId") renterId: Long
    ): Response<List<PaymentReminderDto>>
}
