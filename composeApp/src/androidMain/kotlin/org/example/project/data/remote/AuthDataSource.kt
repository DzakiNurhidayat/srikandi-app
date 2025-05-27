package org.example.project.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import org.example.project.utils.TokenManager
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val tokenManager: TokenManager

) {
    suspend fun getGoogleIdToken(context: Context, webClientId: String): String? {
        return try {
            val credentialManager = CredentialManager.create(context)
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(
                    GetGoogleIdOption.Builder()
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()
            val result =
                credentialManager.getCredential(context, request)
            (result.credential as? GoogleIdTokenCredential)?.idToken
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signInWithGoogleCredential(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user
        } catch (e: Exception) {
            throw e
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun getCurrentUserIdToken(): String {
        val currentUser = firebaseAuth.currentUser
            ?: throw IllegalStateException("User belum login")

        return suspendCoroutine { continuation ->
            currentUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        tokenManager.saveAuthToken(token)
                        continuation.resume(token ?: throw IllegalStateException("ID Token tidak ditemukan"))
                    } else {
                        tokenManager.clearAuthToken()
                        continuation.resumeWithException(task.exception ?: Exception("Gagal mendapatkan ID Token"))
                    }
                }
        }
    }
}