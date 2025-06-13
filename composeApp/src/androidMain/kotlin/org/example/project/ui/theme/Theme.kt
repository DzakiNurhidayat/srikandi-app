package org.example.project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    secondary = secondary,
    error = error,
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer
)

val Shapes = Shapes()

@Composable
fun SrikandiAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}