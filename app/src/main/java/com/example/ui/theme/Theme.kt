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

private val PlayfulDarkColorScheme = darkColorScheme(
    primary = PlayfulPurplePrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = PlayfulPurpleContainer,
    secondary = SkyBlueSecondaryLight,
    onSecondary = Color(0xFF0C4A6E),
    secondaryContainer = Color(0xFF0369A1),
    onSecondaryContainer = SkyBlueContainer,
    tertiary = SunnyYellowLight,
    onTertiary = Color(0xFF78350F),
    background = StarryNightBg,
    surface = StarryNightSurface,
    surfaceVariant = StarryNightSurfaceVariant,
    onBackground = StarryNightOnSurface,
    onSurface = StarryNightOnSurface,
    outline = StarryNightOutline,
    error = RoseError
)

private val PlayfulLightColorScheme = lightColorScheme(
    primary = PlayfulPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PlayfulPurpleContainer,
    onPrimaryContainer = PlayfulPurpleOnContainer,
    secondary = SkyBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = SkyBlueContainer,
    onSecondaryContainer = SkyBlueOnContainer,
    tertiary = SunnyYellow,
    onTertiary = Color.White,
    tertiaryContainer = SunnyYellowContainer,
    onTertiaryContainer = SunnyYellowOnContainer,
    background = WarmBgLight,
    surface = WarmSurfaceLight,
    surfaceVariant = WarmSurfaceVariantLight,
    onBackground = WarmOnSurfaceLight,
    onSurface = WarmOnSurfaceLight,
    outline = WarmOutlineLight,
    error = RoseError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Always enforce our custom playful kid-friendly theme!
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> PlayfulDarkColorScheme
        else -> PlayfulLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
