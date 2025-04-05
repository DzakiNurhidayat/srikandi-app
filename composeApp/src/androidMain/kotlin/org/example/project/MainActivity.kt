package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.example.project.ui.navigation.navGraph
import org.example.project.ui.theme.SrikandiAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SrikandiAppTheme {
                navGraph(navController)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AppAndroidPreview() {
//    App()
//}