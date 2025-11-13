package com.example.lvlupbackend.model.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    val nombre: String,

    @field:NotBlank(message = "El apellido es obligatorio")
    val apellido: String,

    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "Formato de correo inválido")
    val correo: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    @field:Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    val contrasena: String,

    @field:NotBlank(message = "El teléfono es obligatorio")
    val telefono: String,

    val fechaNacimiento: Long,

    @field:NotBlank(message = "La dirección es obligatoria")
    val direccion: String,

    @field:NotBlank(message = "El RUT es obligatorio")
    @field:Pattern(
        regexp = "^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$",
        message = "Formato de RUT inválido (ej: 12.345.678-9)"
    )
    val rut: String,

    @field:NotBlank(message = "La región es obligatoria")
    val region: String,

    @field:NotBlank(message = "La comuna es obligatoria")
    val comuna: String
)

