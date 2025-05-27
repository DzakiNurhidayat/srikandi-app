package org.example.project.data.remote

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import org.example.project.utils.TokenManager
import javax.inject.Inject

class FcmDataSource @Inject constructor(private val tokenManager: TokenManager) {
    suspend fun getFcmToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }
    }

    fun saveFcmToken(token: String) {
        tokenManager.saveFcmToken(token)
    }
}