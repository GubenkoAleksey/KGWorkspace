package com.hubenko.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FirebaseBlueDark,
    onPrimary = FirebaseBackgroundDark,
    background = FirebaseBackgroundDark,
    surface = FirebaseSurfaceDark,
    onSurface = FirebaseOnSurfaceDark,
    surfaceVariant = FirebaseOutlineDark,
    onSurfaceVariant = FirebaseOnSurfaceDark,
    outline = FirebaseOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = FirebaseBlueLight,
    onPrimary = FirebaseSurfaceLight,
    background = FirebaseBackgroundLight,
    surface = FirebaseSurfaceLight,
    onSurface = FirebaseOnSurfaceLight,
    surfaceVariant = FirebaseOutlineLight,
    onSurfaceVariant = FirebaseOnSurfaceLight,
    outline = FirebaseOutlineLight
)

@Composable
fun CoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to strictly follow Firebase style
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
