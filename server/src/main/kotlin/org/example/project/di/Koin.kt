package org.example.project.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import org.example.project.domain.services.inmemory.EvidenceService
import org.example.project.domain.services.inmemory.FormSatuService
import org.example.project.domain.services.inmemory.ProductService
import org.example.project.domain.services.inmemory.ReportService
import org.example.project.firebase.FirebaseRepository
import org.example.project.firebase.FirebaseService
import org.example.project.firebase.NotificationService
import org.example.project.infastructure.repositories.inmemory.EvidenceRepository
import org.example.project.infastructure.repositories.inmemory.FormSatuRepository
import org.example.project.infastructure.repositories.inmemory.ProductRepository
import org.example.project.infastructure.repositories.inmemory.ReportRepository
import org.example.project.infastructure.repositories.interfaces.IEvidenceRepository
import org.example.project.infastructure.repositories.interfaces.IFormSatuRepository
import org.example.project.infastructure.repositories.interfaces.IProductRepository
import org.example.project.infastructure.repositories.interfaces.IReportRepository
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val firebaseModule = module {
    single { FirebaseRepository() }
    single { FirebaseService(get()) }
    single { NotificationService(get(), get(), get()) }
}

fun Application.configureDI() {
    val appModule = module {
        single<Application> { this@configureDI }
        single {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }
        }
    }
    install(Koin) {
        slf4jLogger()
        modules(productModule, reportModule, formSatuModule, firebaseModule, appModule)
    }
}

val productModule = module {
    single<IProductRepository> { ProductRepository() }
    single { ProductService(get()) }
}

val reportModule = module {
    single<IReportRepository> { ReportRepository() }
    single<IEvidenceRepository> { EvidenceRepository() }
    single { ReportService(get(), get()) }
    single { EvidenceService(get()) }
}

val formSatuModule = module {
    single<IFormSatuRepository> { FormSatuRepository() }
    single { FormSatuService(get(), get()) }
}

