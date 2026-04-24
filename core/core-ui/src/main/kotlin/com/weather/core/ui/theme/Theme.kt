package com.weather.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WeatherColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = SkyBlue,
    onPrimaryContainer = NightBlue,
    secondary = SkyBlue,
    background = SurfaceVariant,
    surface = CardBackground,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = CloudGray,
)

@Composable
fun WeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WeatherColorScheme,
        typography = WeatherTypography,
        content = content
    )
}
