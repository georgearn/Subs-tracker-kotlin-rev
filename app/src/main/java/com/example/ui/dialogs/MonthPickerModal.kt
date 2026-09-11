package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.LocalStrings
import com.example.theme.Brand
import com.example.theme.CardBg
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import java.time.Month

@Composable
fun MonthPickerModal(
    currentYear: Int,
    currentMonth: Int,
    onDismiss: () -> Unit,
    onSelectMonthYear: (year: Int, month: Int) -> Unit
) {
    val strings = LocalStrings.current
    var selectedYear by remember { mutableIntStateOf(currentYear) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("month_picker_dialog"),
            color = CardBg
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.selectMonthTitle,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Year Stepper
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBgElevated)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { selectedYear-- },
                        modifier = Modifier.testTag("prev_year_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev Year", tint = TextWhite)
                    }

                    Text(
                        text = selectedYear.toString(),
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { selectedYear++ },
                        modifier = Modifier.testTag("next_year_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4 Rows of 3 Months
                val months = Month.values()
                for (row in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (col in 0 until 3) {
                            val monthIndex = row * 3 + col
                            val month = months[monthIndex]
                            val isSelected = (selectedYear == currentYear && month.value == currentMonth)
                            val monthName = strings.getMonthName(month.value, short = true)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Brand else CardBgElevated)
                                    .clickable {
                                        onSelectMonthYear(selectedYear, month.value)
                                        onDismiss()
                                    }
                                    .padding(vertical = 14.dp)
                                    .testTag("month_btn_${month.value}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = monthName,
                                    color = if (isSelected) Color.White else TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                    if (row < 3) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
