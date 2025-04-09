package org.example.project.common

object ServerConfig {
    const val SERVER_PORT = 8080
    const val SERVER_HOST = "0.0.0.0"
    val SERVER_ANDROID = System.getenv("SERVER_ANDROID") ?: "10.0.2.2"
    const val API_VERSION = "v1"
}

object DatabaseConfig {
    const val DATABASE_URL = "jdbc:postgresql://localhost:5432/srikandi"
    const val DATABASE_DRIVER = "org.postgresql.Driver"
    val DATABASE_USER = System.getenv("DATABASE_USER") ?: "rebar"
    val DATABASE_PASSWORD = System.getenv("DATABASE_PASSWORD") ?:  ""
}