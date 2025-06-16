package org.example.project

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.example.project.application.plugins.configureAuth
import org.example.project.application.plugins.configureCORS
import org.example.project.application.plugins.configureRouting
import org.example.project.application.plugins.configureSerialization
import org.example.project.di.configureDI
import org.example.project.infastructure.DatabaseFactory
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val environmentName = environment.config.propertyOrNull("ktor.environment")?.getString() ?: "unknown"
    val logger = LoggerFactory.getLogger("Application")

    logger.info("Starting application in $environmentName environment")

    try {
        DatabaseFactory.init(environment.config)
        configureAuth(environment.config)
        configureDI()
        configureSerialization()
        configureRouting()
        configureCORS()
    } catch (e: Exception) {
        logger.error("Failed to initialize application: ${e.message}", e)
        throw e
    }
}
