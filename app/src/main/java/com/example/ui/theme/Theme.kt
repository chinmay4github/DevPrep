package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ModernDarkColorScheme = darkColorScheme(
    primary = ModernIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = ModernIndigoContainerDark,
    onPrimaryContainer = ModernIndigoOnContainer,
    secondary = ModernCyan,
    onSecondary = Color(0xFF083344),
    secondaryContainer = ModernCyanContainerDark,
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = ModernAmber,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = ModernAmberContainerDark,
    onTertiaryContainer = Color(0xFFFDE68A),
    background = SlateBackgroundDark,
    surface = SlateSurfaceDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onBackground = SlateOnBackgroundDark,
    onSurface = SlateOnSurfaceDark,
    onSurfaceVariant = SlateOnSurfaceVariantDark,
    outline = SlateOutlineDark,
    error = ModernRose
)

private val ModernLightColorScheme = lightColorScheme(
    primary = ModernIndigoLight,
    onPrimary = Color.White,
    primaryContainer = ModernIndigoContainerLight,
    onPrimaryContainer = Color(0xFF312E81),
    secondary = ModernCyanDark,
    onSecondary = Color.White,
    secondaryContainer = ModernCyanContainerLight,
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = ModernAmberDark,
    onTertiary = Color.White,
    tertiaryContainer = ModernAmberContainerLight,
    onTertiaryContainer = Color(0xFF78350F),
    background = SlateBackgroundLight,
    surface = SlateSurfaceLight,
    surfaceVariant = SlateSurfaceVariantLight,
    onBackground = SlateOnBackgroundLight,
    onSurface = SlateOnSurfaceLight,
    onSurfaceVariant = SlateOnSurfaceVariantLight,
    outline = SlateOutlineLight,
    error = ModernRose
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ModernDarkColorScheme
        else -> ModernLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
