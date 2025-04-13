package org.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.project.common.ServerConfig
import org.example.project.infastructure.DatabaseFactory
import org.example.project.di.configureDI
import org.example.project.application.plugins.configureRouting
import org.example.project.application.plugins.configureSerialization

fun main() {
    embeddedServer(
        Netty,
        port = ServerConfig.SERVER_PORT,
        host = ServerConfig.SERVER_HOST
    ) {
        module()
    }.start(wait = true)
}
fun Application.module() {
    DatabaseFactory.init()
    configureDI()
    configureSerialization()
    configureRouting()
}
