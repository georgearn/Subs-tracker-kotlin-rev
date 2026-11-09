package com.example.logic

import kotlin.math.PI

data class GaugeSegment(val startAngle: Float, val sweepAngle: Float)

/**
 * Lays out half-donut segments so that round-capped strokes leave a visible gap.
 * A round cap extends past the stroke end by half the stroke width, so the gap must
 * be widened by the cap's angular size, otherwise neighbouring segments merge.
 */
fun computeGaugeSegments(
    percentages: List<Float>,
    radius: Float,
    strokeWidth: Float,
    visibleGap: Float
): List<GaugeSegment> {
    val radToDeg = 180f / PI.toFloat()
    val capDegrees = strokeWidth / 2f / radius * radToDeg
    val gapDegrees = visibleGap / radius * radToDeg
    val totalGap = 2f * capDegrees + if (percentages.size > 1) gapDegrees else 0f

    var currentAngle = 180f
    val result = ArrayList<GaugeSegment>(percentages.size)
    for (percentage in percentages) {
        val rawSweep = percentage * 180f
        // Sweep near 0 renders as a round dot, the smallest legal segment.
        val sweep = (rawSweep - totalGap).coerceAtLeast(0.01f)
        val inset = (rawSweep - sweep) / 2f
        result += GaugeSegment(currentAngle + inset, sweep)
        currentAngle += rawSweep
    }
    return result
}
