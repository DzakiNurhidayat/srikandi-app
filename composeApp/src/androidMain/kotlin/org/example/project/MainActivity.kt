package org.example.project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.firebase.FcmTokenManager
import org.example.project.ui.navigation.navGraph
import org.example.project.ui.theme.SrikandiAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchFcmToken()
        setContent {
            navController = rememberNavController()
            handleNotificationIntent(intent, navController)
            SrikandiAppTheme {
                navGraph(navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent, navController)
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("FCM Token: $token")
                CoroutineScope(Dispatchers.IO).launch {
                    sendTokenToServer("user123", token)}
            } else {
                println("Failed to fetch FCM Token: ${task.exception?.message}")
            }
        }
    }

    private suspend fun sendTokenToServer(userId: String, token: String) {
        FcmTokenManager.registerToken(userId, token)
    }

    private fun handleNotificationIntent(intent: Intent?, navController: NavHostController) {
        intent?.let {
            val notificationData = it.getStringExtra("notification_data")
            val action = it.getStringExtra("action")
            val featureId = it.getStringExtra("feature_id")
            when {
                action == "open_feature" && featureId != null -> {
                    navController.navigate("feature_screen/$featureId") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                notificationData != null -> {
                    navController.navigate("notification_screen/$notificationData")
                }
            }
        }
    }
}