package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import java.time.LocalDate

@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasPayments: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val dayOfWeek = strings.formatDayOfWeek(date)
    val dayNumber = date.dayOfMonth.toString()

    val bg = when {
        isSelected -> Brand
        isToday -> CardBgElevated
        else -> CardBg
    }

    val borderModifier = if (isToday && !isSelected) {
        Modifier.border(1.5.dp, Brand.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
    } else Modifier

    Box(
        modifier = modifier
            .width(52.dp)
            .height(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .then(borderModifier)
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 8.dp)
            .testTag("day_cell_${date.dayOfMonth}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayOfWeek,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else TextMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dayNumber,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (hasPayments) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Brand)
                )
            } else {
                Spacer(modifier = Modifier.size(6.dp))
            }
        }
    }
}
