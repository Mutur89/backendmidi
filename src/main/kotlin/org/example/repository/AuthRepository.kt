package com.example.midiventaslvlup.repository

import com.example.lvlupbackend.model.dto.request.LoginRequest
import com.example.lvlupbackend.model.dto.request.RegisterRequest
import com.example.lvlupbackend.model.dto.response.LoginResponse
import com.example.midiventaslvlup.network.RetrofitClient

class AuthRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}