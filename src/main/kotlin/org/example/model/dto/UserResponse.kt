package com.example.lvlupbackend.model.dto.response

data class UserResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val telefono: String,
    val fechaNacimiento: Long,
    val direccion: String,
    val rut: String,
    val region: String,
    val comuna: String
)
