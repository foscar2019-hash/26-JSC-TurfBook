package com.example.turfbook.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = StadiumBgDark,
    primaryContainer = EmeraldDeepest,
    onPrimaryContainer = TextPrimary,
    secondary = AmberGold,
    onSecondary = StadiumBgDark,
    secondaryContainer = AmberDark,
    onSecondaryContainer = TextPrimary,
    tertiary = NightFloodlightBlue,
    onTertiary = TextPrimary,
    background = StadiumBgDark,
    onBackground = TextPrimary,
    surface = StadiumSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = StadiumCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = StadiumBorder
)

@Composable
fun TurfBookTheme(
    darkTheme: Boolean = true, // We default to premium stadium night theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = EmeraldDeepest.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
