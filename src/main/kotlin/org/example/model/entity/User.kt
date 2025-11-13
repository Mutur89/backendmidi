package org.example.org.example.model

import jakarta.persistence.*
import jakarta.persistence.UniqueConstraint
import org.example.org.example.model.enum.UserRole
import java.time.LocalDateTime


@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["correo"])])
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nombre: String,

    @Column(nullable = false)
    val apellido: String,

    @Column(nullable = false, unique = true)
    val correo: String,

    //La contrase;a se parara a base64
    @Column(nullable = false)
    val contrasena: String,

    @Column(nullable = false)
    val telefono: String,

    @Column(nullable = false)
    val fechaNacimiento: Long,

    @Column(nullable = false)
    val direccion: String,

    @Column(nullable = false, unique = true)
    val rut: String,

    @Column(nullable = false)
    val region: String,

    @Column(nullable = false)
    val comuna: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val rol: UserRole = UserRole.CLIENTE,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()


)

