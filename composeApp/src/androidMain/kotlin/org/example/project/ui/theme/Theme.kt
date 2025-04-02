package org.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    // add other colors as needed
)

private val LightColorScheme = lightColorScheme(
    background = LightBackground,

    // add other colors as needed
)

val Typography = androidx.compose.material3.Typography(
    // add your custom typography definitions here
)

val Shapes = androidx.compose.material3.Shapes(
    // add your custom shapes definitions here
)

@Composable
fun SrikandiAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}