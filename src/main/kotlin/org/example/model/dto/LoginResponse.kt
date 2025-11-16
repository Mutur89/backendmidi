package com.example.lvlupbackend.model.dto.response

import org.example.org.example.model.enum.UserRole

//

data class LoginResponse(
    val token: String,
    val type: String = "Bearer",
    val userId: Long,
    val correo: String,
    val nombre: String,
    val rol: UserRole
)

