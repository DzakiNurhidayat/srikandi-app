package org.example.project.firebase

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.example.project.common.enums.StatusLaporan
import org.slf4j.LoggerFactory

class NotificationService(
    private val tokenProvider: FirebaseConfig,
    private val client: HttpClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    suspend fun notifyUserStatusUpdated(userId: String, token: String, statusLaporan: StatusLaporan) {
        val isUser = userId == "user123"
        val title = "Laporan ${statusLaporan.label}"
        val message = if (isUser) statusLaporan.user else statusLaporan.ketua
        sendFcmNotification(token, title, message)
    }

    suspend fun notifyCustom(userId: String, token: String, title: String, message: String, application: Application) {
        sendFcmNotification(token, title, message)
    }

    private suspend fun sendFcmNotification(
        token: String,
        title: String,
        body: String,
        customData: Map<String, String> = emptyMap()
    ) {
        val accessToken = tokenProvider.getAccessToken()
        val projectId = "srikandi-app"

        val message = buildJsonObject {
            putJsonObject("message") {
                put("token", token)
                putJsonObject("notification") {
                    put("title", title)
                    put("body", body)
                }
                putJsonObject("android") {
                    putJsonObject("notification") {
                        put("icon", "satgas_ppkpt")
                        put("color", "#FF6200EE")
                        put("sound", "default")
                        put("click_action", "OPEN_MAIN_ACTIVITY")
                    }
                }
                if (customData.isNotEmpty()) {
                    putJsonObject("data") {
                        customData.forEach { (key, value) ->
                            put(key, value)
                        }
                    }
                }
            }
        }

        val response = client.post("https://fcm.googleapis.com/v1/projects/$projectId/messages:send") {
            header("Authorization", "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(message)
        }

        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            logger.info("FCM message sent successfully: $responseBody")
        } else {
            val errorBody = response.bodyAsText()
            logger.error("Failed to send FCM message: HTTP ${response.status.value}, Body: $errorBody")
            throw IllegalStateException("Failed to send FCM message: ${response.status}")
        }
    }
}

