package com.example.sweetbites.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Pink = Color(0xFFE75480)
private val Cream = Color(0xFFFFF7F8)
private val Ink = Color(0xFF4B2634)

@Composable
fun SweetBitesTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) darkColorScheme(
        primary = Color(0xFFFF8FB1),
        secondary = Color(0xFFFFC3D5),
        background = Color(0xFF20171B),
        surface = Color(0xFF2D2026),
        onPrimary = Color(0xFF4C1027),
        onBackground = Color(0xFFFFEAF0),
        onSurface = Color(0xFFFFEAF0),
        secondaryContainer = Color(0xFF573040)
    ) else lightColorScheme(
        primary = Pink,
        onPrimary = Color.White,
        secondary = Color(0xFF9B596E),
        background = Cream,
        surface = Color(0xFFFFFBFC),
        onBackground = Ink,
        onSurface = Ink,
        surfaceVariant = Color(0xFFF9E8EE),
        onSurfaceVariant = Color(0xFF76515E),
        secondaryContainer = Color(0xFFFFE0E9),
        tertiary = Color(0xFFB76D4C)
    )
    MaterialTheme(colorScheme = colors, typography = MaterialTheme.typography, content = content)
}
