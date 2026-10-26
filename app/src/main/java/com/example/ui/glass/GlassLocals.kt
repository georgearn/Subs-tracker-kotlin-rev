package com.example.ui.glass

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Space scrolling content must leave at the bottom so it can pass under the floating bar. */
val LocalBottomBarInset = compositionLocalOf { 0.dp }

val FloatingBarHeight: Dp = 64.dp
val FloatingBarMargin: Dp = 12.dp
