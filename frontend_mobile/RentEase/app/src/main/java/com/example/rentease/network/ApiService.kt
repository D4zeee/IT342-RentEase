package com.example.rentease.network

import com.example.rentease.model.Room
import com.example.rentease.model.RentedUnit
import com.example.rentease.model.RentedUnitRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/renters/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("/api/renters")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

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
    suspend fun deleteRenter(@Header("Authorization") token: String): Response<Map<String, Any>>

    @GET("/rooms")
    suspend fun getAllRooms(
        @Header("Authorization") token: String
    ): Response<List<Room>>

    @GET("/rooms/{roomId}")
    suspend fun getRoomById(@Path("roomId") roomId: Long): Response<Room>

    @GET("/rented_units")
    suspend fun getAllRentedUnits(): Response<List<RentedUnit>>

    @POST("/rented_units")
    suspend fun createRentedUnit(
        @Body rentedUnit: RentedUnit,
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

    @POST("/rented_units")
    suspend fun createRentedUnit(
        @Body rentedUnit: RentedUnitRequest,
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

}
