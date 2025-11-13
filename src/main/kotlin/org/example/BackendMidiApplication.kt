package org.example.org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackendMidiApplication

fun main(args: Array<String>) {
    runApplication<BackendMidiApplication>(*args)
}