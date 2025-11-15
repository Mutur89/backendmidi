package com.example.midiventaslvlup.network

import com.example.lvlupbackend.model.dto.request.LoginRequest
import com.example.lvlupbackend.model.dto.request.RegisterRequest
import com.example.lvlupbackend.model.dto.request.UserCreateRequest
import com.example.lvlupbackend.model.dto.response.LoginResponse
import com.example.lvlupbackend.model.dto.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<Void> // Se mantiene Response<Void> si el backend no devuelve cuerpo

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse // Se quita el Response<>

    @POST("users")
    suspend fun createUser(@Body user: UserCreateRequest): UserResponse

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): UserResponse

    @GET("users")
    suspend fun getUsers(): List<UserResponse>
}