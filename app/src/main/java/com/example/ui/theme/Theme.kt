package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PsyPrimary,
    onPrimary = PsyOnPrimary,
    primaryContainer = PsyPrimaryContainer,
    onPrimaryContainer = PsyOnPrimaryContainer,
    secondary = PsySecondary,
    onSecondary = PsyOnSecondary,
    secondaryContainer = PsySecondaryContainer,
    onSecondaryContainer = PsyOnSecondaryContainer,
    tertiary = PsyTertiary,
    onTertiary = PsyOnTertiary,
    tertiaryContainer = PsyTertiaryContainer,
    onTertiaryContainer = PsyOnTertiaryContainer,
    background = PsyBackground,
    onBackground = PsyOnBackground,
    surface = PsySurface,
    onSurface = PsyOnSurface,
    surfaceVariant = PsySurfaceVariant,
    onSurfaceVariant = PsyOnSurfaceVariant,
    surfaceContainerLowest = PsySurfaceContainerLowest,
    surfaceContainerLow = PsySurfaceContainerLow,
    surfaceContainer = PsySurfaceContainer,
    surfaceContainerHigh = PsySurfaceContainerHigh,
    surfaceContainerHighest = PsySurfaceContainerHighest,
    error = PsyError,
    onError = PsyOnError,
    errorContainer = PsyErrorContainer,
    onErrorContainer = PsyOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = PsySoftSkyBlue,
    onPrimary = PsyOnPrimaryContainer,
    primaryContainer = PsyPrimary,
    onPrimaryContainer = PsyIceBlue,
    secondary = PsySecondaryContainer,
    onSecondary = PsyOnSecondaryContainer,
    background = PsyOnBackground,
    onBackground = PsyIceBlue,
    surface = PsyOnBackground,
    onSurface = PsyIceBlue,
    error = PsyError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
