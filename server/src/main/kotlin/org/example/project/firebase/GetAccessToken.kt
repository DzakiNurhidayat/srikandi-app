package org.example.project.firebase

import com.google.auth.oauth2.GoogleCredentials
import io.ktor.server.application.*
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.IOException

fun Application.getAccessToken(): String {
    val logger = LoggerFactory.getLogger("AccessTokenProvider")

    val credentialPath = environment.config.propertyOrNull("firebase.fcmCredentialPath")?.getString()
        ?: System.getenv("FIREBASE_CREDENTIAL_PATH")
        ?: throw IllegalStateException("Credentials path not found in application.yaml or environment variables")

    try {
        val credentials = GoogleCredentials.fromStream(FileInputStream(credentialPath))
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        credentials.refreshIfExpired()
        val accessToken = credentials.accessToken?.tokenValue
            ?: throw IllegalStateException("Failed to obtain access token")
        logger.info("Successfully retrieved access token")
        return accessToken
    } catch (e: IOException) {
        logger.error("Failed to read credentials file at $credentialPath: ${e.message}", e)
        throw IllegalStateException("Unable to load credentials: ${e.message}", e)
    } catch (e: Exception) {
        logger.error("Failed to retrieve access token: ${e.message}", e)
        throw IllegalStateException("Error retrieving access token: ${e.message}", e)
    }
}