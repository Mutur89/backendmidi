package com.example.lvlupbackend.model.dto.request

data class UserCreateRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val telefono: String,
    val fechaNacimiento: Long,
    val direccion: String,
    val rut: String,
    val region: String,
    val comuna: String
)
