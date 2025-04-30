package org.example.project.firebase

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.example.project.common.enums.StatusLaporan


class NotificationService(
    private val firebaseService: FirebaseService,
    private val application: Application,
    private val client: HttpClient
) {
    suspend fun notifyUserStatusUpdated(userId: String, statusLaporan: StatusLaporan) {
        val isUser = userId == "user123"
        val token = firebaseService.getToken(userId)
        val title = "Laporan ${statusLaporan.label}"
        val message = if (isUser) statusLaporan.user else statusLaporan.ketua
        sendFcmNotification(token, title, message)
    }

    suspend fun notifyCustom(userId: String, title: String, message: String, application: Application) {
        val token = firebaseService.getToken(userId)
        sendFcmNotification(token, title, message)
    }

    private suspend fun sendFcmNotification(
        token: String,
        title: String,
        body: String,
        customData: Map<String, String> = emptyMap()
    ) {
        val accessToken = application.getAccessToken()
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
        client.close()
    }
}

