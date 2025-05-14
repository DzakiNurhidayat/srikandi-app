package org.example.project.data.repositories

import android.content.SharedPreferences
import org.example.project.data.remote.AuthTokenProvider
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AuthTokenProvider {
    override fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
}
