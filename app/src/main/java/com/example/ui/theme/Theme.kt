package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = UzbekIndigoPrimaryDark,
    onPrimary = UzbekIndigoOnPrimaryDark,
    primaryContainer = UzbekPrimaryContainerDark,
    onPrimaryContainer = UzbekOnPrimaryContainerDark,
    secondary = UzbekGoldSecondaryDark,
    onSecondary = UzbekGoldOnSecondaryDark,
    secondaryContainer = UzbekSecondaryContainerDark,
    onSecondaryContainer = UzbekOnSecondaryContainerDark,
    tertiary = UzbekTurquoiseTertiaryDark,
    onTertiary = UzbekOnTertiaryDark,
    background = UzbekBackgroundDark,
    onBackground = UzbekOnBackgroundDark,
    surface = UzbekSurfaceDark,
    onSurface = UzbekOnSurfaceDark,
    surfaceVariant = UzbekSurfaceVariantDark,
    onSurfaceVariant = UzbekOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = UzbekIndigoPrimary,
    onPrimary = UzbekIndigoOnPrimary,
    primaryContainer = UzbekPrimaryContainer,
    onPrimaryContainer = UzbekOnPrimaryContainer,
    secondary = UzbekGoldSecondary,
    onSecondary = UzbekGoldOnSecondary,
    secondaryContainer = UzbekSecondaryContainer,
    onSecondaryContainer = UzbekOnSecondaryContainer,
    tertiary = UzbekTurquoiseTertiary,
    onTertiary = UzbekOnTertiary,
    tertiaryContainer = UzbekTertiaryContainer,
    onTertiaryContainer = UzbekOnTertiaryContainer,
    background = UzbekBackgroundLight,
    onBackground = UzbekOnBackgroundLight,
    surface = UzbekSurfaceLight,
    onSurface = UzbekOnSurfaceLight,
    surfaceVariant = UzbekSurfaceVariantLight,
    onSurfaceVariant = UzbekOnSurfaceVariantLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep tailored cultural theme primary
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
        content = content
    )
}
