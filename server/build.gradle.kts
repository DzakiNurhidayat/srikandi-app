plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example.project"
version = "1.0.0"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "true"}")
}

tasks.withType<Jar> {
    from(sourceSets.main.get().resources)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.shared)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.config.yaml)

    // Exposed dependencies
    implementation(libs.exposed.core) // Core API untuk DSL SQL
    implementation(libs.exposed.jdbc) // Integrasi JDBC
    implementation(libs.exposed.java.time) // Dukungan untuk tipe waktu Java
    implementation(libs.exposed.dao) // Dukungan untuk DAO (Data Access Object)

    // Database connection pools and drivers
    implementation(libs.hikaricp) // HikariCP untuk pooling koneksi
    implementation(libs.postgresql) // Driver PostgreSQL
    implementation(libs.h2) // Database H2 untuk pengujian

    implementation(libs.kotlinx.datetime)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.google.auth.library.oauth2.http)
    implementation(libs.firebase.admin)
    implementation(libs.ktor.server.cors)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}