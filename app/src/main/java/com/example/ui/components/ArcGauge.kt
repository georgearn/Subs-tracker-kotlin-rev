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

private val GAUGE_WIDTH = 260.dp
private val GAUGE_HEIGHT = 146.dp
private val STROKE_WIDTH = 28.dp
private val SEGMENT_GAP = 6.dp
private const val MIN_SWEEP = 0.1f

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
    val trackColor = CardBgElevated

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .testTag("arc_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(GAUGE_WIDTH, GAUGE_HEIGHT)) {
            val strokeWidth = STROKE_WIDTH.toPx()
            val radius = (size.width - strokeWidth) / 2f
            val arcSize = Size(radius * 2f, radius * 2f)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
            val style = Stroke(width = strokeWidth, cap = StrokeCap.Round)

            // Round caps overhang each sweep by half a stroke; shrink sweeps by that angle.
            val capDegrees = Math.toDegrees((strokeWidth / 2f / radius).toDouble()).toFloat()
            val gapDegrees = Math.toDegrees((SEGMENT_GAP.toPx() / radius).toDouble()).toFloat()
            val trackStart = 180f + capDegrees
            val trackSweep = 180f - 2f * capDegrees

            drawArc(
                color = trackColor,
                startAngle = trackStart,
                sweepAngle = trackSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = style
            )

            val visible = categorySpends.filter { it.percentage > 0f }
            if (totalSpend > 0 && visible.isNotEmpty()) {
                val drawable = trackSweep - gapDegrees * (visible.size - 1)
                var currentAngle = trackStart
                for (item in visible) {
                    val slice = item.percentage * drawable
                    drawArc(
                        color = getCategoryColor(item.category),
                        startAngle = currentAngle,
                        sweepAngle = slice.coerceAtLeast(MIN_SWEEP),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = style
                    )
                    currentAngle += slice + gapDegrees
                }
            }
        }

        // Center Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
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
