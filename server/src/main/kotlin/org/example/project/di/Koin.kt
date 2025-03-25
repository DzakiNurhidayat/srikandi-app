package org.example.project.di

import io.ktor.server.application.*
import org.example.project.domain.services.inmemory.ProductService
import org.example.project.infastructure.repositories.inmemory.ProductRepository
import org.example.project.infastructure.repositories.interfaces.IProductRepository
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
    single<IProductRepository> { ProductRepository() }
    single{ ProductService(get()) }
}
