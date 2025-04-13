package org.example.project.firebase

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.example.project.common.ServerConfig

expect fun getHttpClientEngine(): HttpClientEngine

object FcmTokenManager {
    private val client = HttpClient(getHttpClientEngine()) {
        expectSuccess = true
    }

    suspend fun registerToken(userId: String, token: String) {
        try {
            val response = client.post("http://${ServerConfig.SERVER_ANDROID}:${ServerConfig.SERVER_PORT}/firebase/register-token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("userId=$userId&token=$token")
            }
            println("Token registration response: ${response.bodyAsText()}")
        } catch (e: Exception) {
            println("Failed to register token: ${e.message}")
            e.printStackTrace()
        }
    }
}