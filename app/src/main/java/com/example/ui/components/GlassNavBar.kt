package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.ui.glass.FloatingBarHeight
import com.example.ui.navigation.AppTab
import io.github.itshedimisawi.vitro.Glass
import io.github.itshedimisawi.vitro.GlassBackdrop
import io.github.itshedimisawi.vitro.glassEffect
import io.github.itshedimisawi.vitro.rememberGlassEffectState

@Composable
fun GlassNavBar(
    backdrop: GlassBackdrop,
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val glassState = rememberGlassEffectState()

    Row(
        modifier = modifier
            .height(FloatingBarHeight)
            .glassEffect(backdrop, Glass.Regular, state = glassState)
            .padding(6.dp)
            .testTag("bottom_nav_bar"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTab.entries.forEach { tab ->
            val selected = tab == currentTab
            val label = tab.label(strings)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(if (selected) Brand else Color.Transparent)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onTabSelected(tab) })
                    .testTag(tab.testTag),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val tint = if (selected) Color.White else glassState.contentColor.copy(alpha = 0.6f)
                Icon(
                    imageVector = tab.icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = label,
                    color = tint,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GlassFab(
    backdrop: GlassBackdrop,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassState = rememberGlassEffectState()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(FloatingBarHeight)
            .glassEffect(
                backdrop = backdrop,
                glass = Glass.Regular.tint(Brand.copy(alpha = 0.75f)).interactive(),
                state = glassState,
                interactionSource = interactionSource
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = contentDescription,
            tint = glassState.contentColor,
            modifier = Modifier.size(28.dp)
        )
    }
}
