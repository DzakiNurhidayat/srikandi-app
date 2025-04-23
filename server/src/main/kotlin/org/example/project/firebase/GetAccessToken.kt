package org.example.project.firebase

import com.google.auth.oauth2.GoogleCredentials
import java.io.InputStream

fun getAccessToken(): String {
    val serviceAccountStream: InputStream? = object {}.javaClass.classLoader
        .getResourceAsStream("firebase/srikandi-app-firebase-adminsdk-fbsvc-5be20261cb.json")

    if (serviceAccountStream == null) {
        throw IllegalStateException("Service account file not found in resources/firebase/")
    }

    val credentials = GoogleCredentials.fromStream(serviceAccountStream)
        .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
    credentials.refreshIfExpired()
    return credentials.accessToken.tokenValue
}