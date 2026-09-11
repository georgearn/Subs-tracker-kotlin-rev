package com.example.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Default Brand Tokens
val DefaultBrand = Color(0xFF7359E0)
val DefaultBrandDark = Color(0xFF4D389E)
val DefaultBrandLight = Color(0xFF907BF0)

val DefaultTotalBg = Color(0xFF4D4D57)
val GreenSuccess = Color(0xFF2E9E57)
val BlueAccent = Color(0xFF3373B2)
val RedAlert = Color(0xFFBD4040)
val NeutralBg = Color(0xFF3D3D4A)

// Dark Palette
val DarkWindowBg = Color(0xFF0C0C0F)
val DarkCardBg = Color(0xFF262630)
val DarkCardBgElevated = Color(0xFF32323E)
val DarkTextWhite = Color(0xFFF2F2F2)
val DarkTextMuted = Color(0xFF9E9EAA)

// Light Palette (Warm / Crisp Neutral)
val LightWindowBg = Color(0xFFF6F7FB)
val LightCardBg = Color(0xFFFFFFFF)
val LightCardBgElevated = Color(0xFFECEFF5)
val LightTextWhite = Color(0xFF191C24)
val LightTextMuted = Color(0xFF6E7280)
val LightTotalBg = Color(0xFFD6D9E3)

// Dynamic theme accessors
val Brand: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.brand
val BrandDark: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.brandDark
val BrandLight: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.brandLight
val TotalBg: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.totalBg
val WindowBg: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.windowBg
val CardBg: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.cardBg
val CardBgElevated: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.cardBgElevated
val TextWhite: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.textWhite
val TextMuted: Color @Composable @ReadOnlyComposable get() = AppTheme.colors.textMuted

val CategoryColors = mapOf(
    "Entertainment" to Color(0xFFD97333),
    "Essentials" to Color(0xFF338CBF),
    "Productivity" to Color(0xFF4DA673),
    "Quality of Life" to Color(0xFF9973CC),
    "Streaming" to Color(0xFFCC4073),
    "Random" to Color(0xFFBFB333),
    "Other" to Color(0xFF8C8C94)
)

val MetallicPalette = listOf(
    Color(0xFF9496A1), // silver
    Color(0xFF545761), // gunmetal
    Color(0xFFA3946B), // champagne gold
    Color(0xFF707580), // steel
    Color(0xFF665961), // rose gunmetal
    Color(0xFF4D5C66)  // slate
)

fun getCategoryColor(name: String?): Color {
    return CategoryColors[name ?: "Other"] ?: Color(0xFF8C8C94)
}

fun getMetallicColor(seed: String?): Color {
    val hash = (seed ?: "").hashCode()
    val index = Math.floorMod(hash, MetallicPalette.size)
    return MetallicPalette[index]
}
