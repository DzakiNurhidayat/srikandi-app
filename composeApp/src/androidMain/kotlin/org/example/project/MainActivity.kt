package org.example.project

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.example.project.ui.navigation.navGraph
import org.example.project.ui.theme.SrikandiAppTheme
import org.example.project.ui.viewmodel.shared.SharedReportViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SrikandiAppTheme {
                val sharedReportViewModel: SharedReportViewModel = hiltViewModel()
                navGraph(navController, sharedReportViewModel)
            }
        }
    }
}