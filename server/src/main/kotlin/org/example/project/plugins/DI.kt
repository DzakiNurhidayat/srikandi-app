package org.example.project.plugins

import io.ktor.server.application.*
import org.example.project.repositories.ProductRepository
import org.example.project.services.ProductService
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(productModule)
    }
}

val productModule = module {
    single { ProductRepository() }
    single { ProductService(get()) }
}

