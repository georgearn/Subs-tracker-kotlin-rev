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
import com.example.theme.TextMuted
import com.example.theme.TextWhite
import com.example.logic.computeGaugeSegments
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .testTag("arc_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(260.dp, 150.dp)) {
            val strokeWidth = 14.dp.toPx()
            val canvasWidth = size.width
            val canvasHeight = size.height
            // A true semicircle needs a *square* bounding box (width == height) — the old
            // math stretched that box taller than it was wide, which is what made the
            // dome look squished/oval instead of a clean round arc.
            val diameter = minOf(canvasWidth, canvasHeight * 2f) - strokeWidth
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset((canvasWidth - diameter) / 2f, strokeWidth / 2f)

            if (totalSpend > 0 && categorySpends.isNotEmpty()) {
                val visible = categorySpends.filter { it.percentage * 180f > 1f }
                val segments = computeGaugeSegments(
                    percentages = visible.map { it.percentage },
                    radius = diameter / 2f,
                    strokeWidth = strokeWidth,
                    visibleGap = 5.dp.toPx()
                )
                visible.zip(segments).forEach { (item, segment) ->
                    drawArc(
                        color = getCategoryColor(item.category),
                        startAngle = segment.startAngle,
                        sweepAngle = segment.sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
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
