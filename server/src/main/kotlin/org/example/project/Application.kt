package org.example.project

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.example.project.application.plugins.configureCORS
import org.example.project.application.plugins.configureRouting
import org.example.project.application.plugins.configureSerialization
import org.example.project.di.configureDI
import org.example.project.firebase.FirebaseConfig
import org.example.project.infastructure.DatabaseFactory
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val environmentName = environment.config.propertyOrNull("server")?.getString() ?: "unknown"
    val logger = LoggerFactory.getLogger("Application")

    logger.info("Starting application in $environmentName environment")

    try {
        DatabaseFactory.init(environment.config)
        configureDI()
        configureSerialization()
        configureRouting()
        configureCORS()
        val firebaseConfig: FirebaseConfig by inject()
        firebaseConfig.initializeFirebase()
    } catch (e: Exception) {
        logger.error("Failed to initialize application: ${e.message}", e)
        throw e
    }
}
