package com.example.provault.Files

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// PinataApiService.kt
interface PinataApiService {
    @GET("files/groups")
    suspend fun fetchGroups(
        @Header("Authorization") authToken: String,
        @Query("name") groupName: String
    ): ApiResponse
}