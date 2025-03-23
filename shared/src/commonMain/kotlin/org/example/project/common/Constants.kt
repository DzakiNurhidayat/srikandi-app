package org.example.project.common

object ServerConfig {
    const val SERVER_PORT = 8080
    const val SERVER_HOST = "0.0.0.0"
    const val API_VERSION = "v1"
//    const val JWT_SECRET = "your-secret-key"
//    const val JWT_ISSUER = "your-application"
//    const val JWT_AUDIENCE = "your-audience"
//    const val JWT_REALM = "your-realm"
}

object DatabaseConfig {
    const val DATABASE_URL = "jdbc:postgresql://localhost:5432/srikandi"
    const val DATABASE_DRIVER = "org.postgresql.Driver"
    const val DATABASE_USER = "rebar"
    const val DATABASE_PASSWORD = ""
}