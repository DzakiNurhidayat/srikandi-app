package org.example.project.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.slf4j.LoggerFactory
import java.io.FileInputStream

class FirebaseConfig(credentialPath: String) {
    private val logger = LoggerFactory.getLogger(FirebaseConfig::class.java)

    private val credentials: GoogleCredentials by lazy {
        FileInputStream(credentialPath).use { fileStream ->
            GoogleCredentials.fromStream(fileStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        }
    }

    fun initializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(
                FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build()
            )
        }
    }

    fun getAccessToken(): String {
        return try {
            credentials.refreshIfExpired()
            credentials.accessToken?.tokenValue
                ?: throw IllegalStateException("Failed to obtain Firebase access token")
        } catch (e: Exception) {
            logger.error("Error getting Firebase access token", e)
            throw IllegalStateException("Error getting Firebase access token", e)
        }
    }
}