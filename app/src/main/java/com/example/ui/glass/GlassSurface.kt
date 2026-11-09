package com.example.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.theme.AppTheme
import io.github.itshedimisawi.vitro.Glass
import io.github.itshedimisawi.vitro.GlassAppearance
import io.github.itshedimisawi.vitro.GlassBackdrop
import io.github.itshedimisawi.vitro.glassEffect

/**
 * Glass on content surfaces (cards, pills, toggles) is created fresh whenever a screen is shown
 * and flashes white for a frame until its backdrop is sampled. While false those surfaces use a
 * translucent flat fill; only the persistent nav bar and FAB use the glass shader.
 */
const val GlassContentSurfaces = false

/** Backdrop that glass surfaces refract; null where no glass host exists (previews, tests). */
val LocalGlassBackdrop = compositionLocalOf<GlassBackdrop?> { null }

/** Subtler than [Glass.Regular] so many cards in a list stay calm and legible. */
val CardGlass: Glass = Glass.Regular.copy(
    refractionHeight = 10.dp,
    refractionAmount = 14.dp,
    shadowRadius = 14.dp,
    shadowOffset = 4.dp,
    shadowOpacity = 0.10f
)

/**
 * Draws [shape] as glass over the shared backdrop, or as a flat [fallback] fill when none exists.
 * Apply `.clip(shape)` after it so ripples and content stay inside the glass.
 */
@Composable
fun Modifier.glassSurface(
    shape: CornerBasedShape,
    fallback: Color,
    glass: Glass = CardGlass
): Modifier {
    if (!GlassContentSurfaces) return this.flatSurface(shape, fallback, alpha = 0.8f)
    val backdrop = LocalGlassBackdrop.current ?: return this.clip(shape).background(fallback)
    return this.glassEffect(backdrop, glass.copy(appearance = themedGlassAppearance()), shape)
}

/** Always-glass variant for the small top controls (toggles, pills); ignores [GlassContentSurfaces]. */
@Composable
fun Modifier.glassControl(shape: CornerBasedShape, fallback: Color): Modifier {
    val backdrop = LocalGlassBackdrop.current ?: return this.clip(shape).background(fallback)
    return this.glassEffect(backdrop, CardGlass.copy(appearance = themedGlassAppearance()), shape)
}

/**
 * Translucent flat fill for items in scrolling or frequently rebuilt lists. Glass there flashes
 * white for a frame when an item first appears, so list items skip the glass shader.
 */
fun Modifier.flatSurface(shape: CornerBasedShape, color: Color, alpha: Float = 0.7f): Modifier =
    this.clip(shape).background(color.copy(alpha = color.alpha * alpha))

/**
 * Pins the glass material to the app theme. With the default automatic appearance a fresh glass
 * starts light until its backdrop has been sampled, which shows as a white flash on tab changes.
 */
@Composable
fun themedGlassAppearance(): GlassAppearance =
    if (AppTheme.colors.isDark) GlassAppearance.Dark else GlassAppearance.Light

/**
 * Dialogs live in their own window, so they cannot refract the main backdrop. This gives them a
 * translucent "frosted" fill instead, so the glass screen stays visible behind them.
 */
fun Color.dialogGlass(): Color = copy(alpha = 0.86f)

/** Thin light rim shared by frosted dialog containers. */
val DialogGlassBorder = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
