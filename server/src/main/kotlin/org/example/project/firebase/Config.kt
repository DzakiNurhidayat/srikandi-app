package org.example.project.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class FirebaseConfig(credentialPath: String) {
    private val logger = LoggerFactory.getLogger(FirebaseConfig::class.java)

    private val credentials: GoogleCredentials by lazy {
        val inputStream: InputStream = if (File(credentialPath).exists()) {
            FileInputStream(credentialPath)
        } else {
            ByteArrayInputStream(credentialPath.toByteArray(Charsets.UTF_8))
        }

        inputStream.use {
            GoogleCredentials.fromStream(it)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        }
    }

    private val initialized by lazy {
        initializeFirebase()
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

    val db: Firestore by lazy {
        initialized
        FirestoreClient.getFirestore()
    }
}
