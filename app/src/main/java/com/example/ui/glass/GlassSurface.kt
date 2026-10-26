package com.example.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.itshedimisawi.vitro.Glass
import io.github.itshedimisawi.vitro.GlassBackdrop
import io.github.itshedimisawi.vitro.glassEffect

/** Backdrop that glass surfaces refract; null where no glass host exists (previews, tests). */
val LocalGlassBackdrop = androidx.compose.runtime.compositionLocalOf<GlassBackdrop?> { null }

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
    val backdrop = LocalGlassBackdrop.current ?: return this.clip(shape).background(fallback)
    return this.glassEffect(backdrop, glass, shape)
}
