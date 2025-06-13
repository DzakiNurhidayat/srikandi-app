package org.example.project.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import org.example.project.utils.TokenManager
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAuthToken()
        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        Log.d("AuthInterceptor", "Token: $token")
        return chain.proceed(requestBuilder.build())
    }
}
