package com.example.lvlupbackend.model.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "Formato de correo inválido")
    val correo: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val contrasena: String
)
