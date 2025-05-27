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
import org.example.project.firebase.FcmService
import org.example.project.firebase.FirebaseConfig
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

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(module {
            single { environment }
        }, appModule, productModule, reportModule, formSatuModule, firebaseModule)
    }
}

val appModule = module {
    single {
        val env = get<ApplicationEnvironment>()
        val credentialPath = env.config.propertyOrNull("firebase.fcmCredentialPath")?.getString()
            ?: System.getenv("FIREBASE_CREDENTIAL_PATH")
            ?: throw IllegalStateException("Firebase credentials path not found")
        FirebaseConfig(credentialPath).apply {
            initializeFirebase()
        }
    }
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}

val firebaseModule = module {
    single { FcmService(get(), get()) }
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

