package com.restoration.core.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(primary = Color(0xFF1976D2), secondary = Color(0xFF26A69A))
private val DarkColors = darkColorScheme(primary = Color(0xFF90CAF9), secondary = Color(0xFF80CBC4))

@Composable
fun RestorationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, typography = Typography(), content = content)
}
