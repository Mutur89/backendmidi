package org.example.org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = [
    "org.example.model.entity",              // Para Cart y CartItem
    "com.example.lvlupbackend.model.entity", // Para Product, Order, OrderItem
    "org.example.org.example.model"          // Para User
])
@EnableJpaRepositories(basePackages = [
    "org.example.org.example.repository"     // Todos los repositories
])
class BackendMidiApplication

fun main(args: Array<String>) {
    runApplication<BackendMidiApplication>(*args)
}