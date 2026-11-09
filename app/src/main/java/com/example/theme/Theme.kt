package com.example.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class AppThemeColors(
    val brand: Color,
    val brandDark: Color,
    val brandLight: Color,
    val totalBg: Color,
    val windowBg: Color,
    val cardBg: Color,
    val cardBgElevated: Color,
    val textWhite: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val LocalAppThemeColors = staticCompositionLocalOf<AppThemeColors> {
    AppThemeColors(
        brand = DefaultBrand,
        brandDark = DefaultBrandDark,
        brandLight = DefaultBrandLight,
        totalBg = DefaultTotalBg,
        windowBg = DarkWindowBg,
        cardBg = DarkCardBg,
        cardBgElevated = DarkCardBgElevated,
        textWhite = DarkTextWhite,
        textMuted = DarkTextMuted,
        isDark = true
    )
}

object AppTheme {
    val colors: AppThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppThemeColors.current
}

@Composable
fun SubscriptionTrackerTheme(
    themeMode: String = "dark", // "dark", "light", "system"
    useMaterialAccent: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode.lowercase()) {
        "light" -> false
        "system" -> isSystemDark
        else -> true
    }

    val context = LocalContext.current

    // Dynamic brand accents based on Material UI accent toggle
    val (effectiveBrand, effectiveBrandDark, effectiveBrandLight) = if (useMaterialAccent) {
        val dynamic = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        Triple(dynamic.primary, dynamic.primaryContainer, dynamic.tertiary)
    } else {
        Triple(DefaultBrand, DefaultBrandDark, DefaultBrandLight)
    }

    val appThemeColors = if (isDark) {
        AppThemeColors(
            brand = effectiveBrand,
            brandDark = effectiveBrandDark,
            brandLight = effectiveBrandLight,
            totalBg = DefaultTotalBg,
            windowBg = DarkWindowBg,
            cardBg = DarkCardBg,
            cardBgElevated = DarkCardBgElevated,
            textWhite = DarkTextWhite,
            textMuted = DarkTextMuted,
            isDark = true
        )
    } else {
        AppThemeColors(
            brand = effectiveBrand,
            brandDark = effectiveBrandDark,
            brandLight = effectiveBrandLight,
            totalBg = LightTotalBg,
            windowBg = LightWindowBg,
            cardBg = LightCardBg,
            cardBgElevated = LightCardBgElevated,
            textWhite = LightTextWhite,
            textMuted = LightTextMuted,
            isDark = false
        )
    }

    val materialColorScheme = if (isDark) {
        if (useMaterialAccent) {
            dynamicDarkColorScheme(context)
        } else {
            darkColorScheme(
                primary = effectiveBrand,
                onPrimary = Color.White,
                primaryContainer = effectiveBrandDark,
                onPrimaryContainer = Color.White,
                secondary = BlueAccent,
                onSecondary = Color.White,
                background = DarkWindowBg,
                onBackground = DarkTextWhite,
                surface = DarkCardBg,
                onSurface = DarkTextWhite,
                surfaceVariant = DarkCardBgElevated,
                onSurfaceVariant = DarkTextMuted,
                surfaceContainer = DarkCardBg,
                surfaceContainerHigh = DarkCardBgElevated,
                error = RedAlert,
                onError = Color.White
            )
        }
    } else {
        if (useMaterialAccent) {
            dynamicLightColorScheme(context)
        } else {
            lightColorScheme(
                primary = effectiveBrand,
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE8DDFF),
                onPrimaryContainer = DefaultBrandDark,
                secondary = BlueAccent,
                onSecondary = Color.White,
                background = LightWindowBg,
                onBackground = LightTextWhite,
                surface = LightCardBg,
                onSurface = LightTextWhite,
                surfaceVariant = LightCardBgElevated,
                onSurfaceVariant = LightTextMuted,
                surfaceContainer = LightCardBg,
                surfaceContainerHigh = LightCardBgElevated,
                error = RedAlert,
                onError = Color.White
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
        }
    }

    CompositionLocalProvider(LocalAppThemeColors provides appThemeColors) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}
