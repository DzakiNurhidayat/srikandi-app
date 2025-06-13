package org.example.project.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val authPrefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val fcmPrefs: SharedPreferences =
        context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ID_TOKEN = "id_token"
        private const val KEY_FCM_TOKEN = "fcm_token"
    }

    // Auth token functions
    fun saveAuthToken(token: String?) {
        authPrefs.edit {
            putString(KEY_ID_TOKEN, token)
        }
    }

    fun getAuthToken(): String? {
        return authPrefs.getString(KEY_ID_TOKEN, null)
    }

    fun clearAuthToken() {
        authPrefs.edit {
            remove(KEY_ID_TOKEN)
        }
    }

    // FCM token functions
    fun saveFcmToken(token: String?) {
        fcmPrefs.edit {
            putString(KEY_FCM_TOKEN, token)
        }
    }

    fun getFcmToken(): String? {
        return fcmPrefs.getString(KEY_FCM_TOKEN, null)
    }

    fun clearFcmToken() {
        fcmPrefs.edit {
            remove(KEY_FCM_TOKEN)
        }
    }
}
