package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theme.CardBgElevated
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.theme.getCategoryColor

data class CategorySpend(
    val category: String,
    val annualAmount: Double,
    val percentage: Float
)

@Composable
fun ArcGauge(
    categorySpends: List<CategorySpend>,
    totalSpend: Double,
    primaryCurrency: String,
    modifier: Modifier = Modifier
) {
    val baseArcColor = CardBgElevated

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .testTag("arc_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp, 160.dp)) {
            val strokeWidth = 26.dp.toPx()
            val canvasWidth = size.width
            val canvasHeight = size.height
            val arcSize = Size(canvasWidth - strokeWidth, (canvasHeight * 1.8f) - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Background base arc (180 degrees from 180 to 360)
            drawArc(
                color = baseArcColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            if (totalSpend > 0 && categorySpends.isNotEmpty()) {
                var currentAngle = 180f
                for (item in categorySpends) {
                    val sweep = item.percentage * 180f
                    if (sweep > 0.5f) {
                        drawArc(
                            color = getCategoryColor(item.category),
                            startAngle = currentAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        currentAngle += sweep
                    }
                }
            }
        }

        // Center Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = "%.2f %s".format(totalSpend, primaryCurrency),
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "yearly spend",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
