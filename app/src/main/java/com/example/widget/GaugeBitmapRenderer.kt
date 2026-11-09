package com.example.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.toArgb
import com.example.logic.computeGaugeSegments
import com.example.theme.DarkTextMuted
import com.example.theme.DarkTextWhite
import com.example.theme.getCategoryColor

/**
 * Draws the same half-donut gauge as [com.example.ui.components.ArcGauge], but onto a plain
 * android.graphics Bitmap — a Glance widget can't host a Compose Canvas, so the gauge is
 * rendered once per refresh and shown as a static image instead.
 */
object GaugeBitmapRenderer {

    fun render(
        categorySlices: List<WidgetDataSource.CategorySlice>,
        totalAnnualSpend: Double,
        currencySymbol: String,
        widthPx: Int = 480,
        heightPx: Int = 340
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val strokePx = widthPx * 0.055f
        val diameter = minOf(widthPx.toFloat(), heightPx * 2f) - strokePx
        val left = (widthPx - diameter) / 2f
        val top = strokePx / 2f
        val rect = RectF(left, top, left + diameter, top + diameter)

        if (totalAnnualSpend > 0 && categorySlices.isNotEmpty()) {
            val visible = categorySlices.filter { it.percentage * 180f > 1f }
            val segmentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = strokePx
                strokeCap = Paint.Cap.ROUND
            }
            val segments = computeGaugeSegments(
                percentages = visible.map { it.percentage },
                radius = diameter / 2f,
                strokeWidth = strokePx,
                visibleGap = strokePx * 0.2f
            )
            visible.zip(segments).forEach { (slice, segment) ->
                segmentPaint.color = getCategoryColor(slice.category).toArgb()
                canvas.drawArc(rect, segment.startAngle, segment.sweepAngle, false, segmentPaint)
            }
        }

        val centerX = widthPx / 2f
        val textBaseY = top + diameter / 2f + strokePx * 0.55f

        val amountPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = DarkTextWhite.toArgb()
            textSize = widthPx * 0.105f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = DarkTextMuted.toArgb()
            textSize = widthPx * 0.058f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("%.0f %s".format(totalAnnualSpend, currencySymbol), centerX, textBaseY, amountPaint)
        canvas.drawText("yearly spend", centerX, textBaseY + widthPx * 0.075f, labelPaint)

        return bitmap
    }
}
