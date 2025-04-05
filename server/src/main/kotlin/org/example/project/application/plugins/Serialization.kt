package org.example.project.application.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.example.project.common.serialization.LocalDateSerializer
import org.example.project.common.serialization.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
            serializersModule = SerializersModule {
                contextual(LocalDate::class, LocalDateSerializer)
                contextual(LocalDateTime::class, LocalDateTimeSerializer)
            }
        })
    }
}
