package com.botta.uno24

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Uno24DialRenderer {

    private val dayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F5F5F7")
        style = Paint.Style.FILL
    }

    private val nightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1D2A3A")
        style = Paint.Style.FILL
    }

    private val dialBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#333333")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val majorTickPaintDay = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A1A")
        style = Paint.Style.STROKE
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
    }

    private val majorTickPaintNight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F5F5F7")
        style = Paint.Style.STROKE
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
    }

    private val minorTickPaintDay = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#757575")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        strokeCap = Paint.Cap.ROUND
    }

    private val minorTickPaintNight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#90A4AE")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaintDay = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A1A")
        textAlign = Paint.Align.CENTER
    }

    private val textPaintNight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F5F5F7")
        textAlign = Paint.Align.CENTER
    }

    private val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D32F2F")
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
    }

    private val centerCapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A1A")
        style = Paint.Style.FILL
    }

    private val rectF = RectF()

    fun draw(
        canvas: Canvas,
        width: Int,
        height: Int,
        timeHourFraction: Double,
        sunTimes: SunTimes
    ) {
        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) / 2f * 0.88f

        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius)

        // 1. Base circular dial background (Day zone)
        canvas.drawCircle(cx, cy, radius, dayPaint)

        // 2. Shaded dark night arc
        val sunsetAngle = timeToAngle(sunTimes.sunsetHour)
        val sunriseAngle = timeToAngle(sunTimes.sunriseHour)

        var nightDuration = sunTimes.sunriseHour - sunTimes.sunsetHour
        if (nightDuration <= 0) {
            nightDuration += 24.0
        }
        val sweepAngle = (nightDuration * 15.0).toFloat()
        val startAngleCanvas = sunsetAngle - 90f

        canvas.drawArc(rectF, startAngleCanvas, sweepAngle, true, nightPaint)

        // Outer dial border
        canvas.drawCircle(cx, cy, radius, dialBorderPaint)

        // Helper to check if an angle falls within night arc
        fun isNight(angle: Float): Boolean {
            val normalizedAngle = (angle % 360f + 360f) % 360f
            val start = (sunsetAngle % 360f + 360f) % 360f
            val end = (sunriseAngle % 360f + 360f) % 360f
            return if (start <= end) {
                normalizedAngle in start..end
            } else {
                normalizedAngle >= start || normalizedAngle <= end
            }
        }

        // 3. Ticks & Numerals
        val textSize = radius * 0.09f
        textPaintDay.textSize = textSize
        textPaintNight.textSize = textSize

        for (h in 0 until 24) {
            // Major tick at integer hour
            val hourAngle = timeToAngle(h.toDouble())
            val inNight = isNight(hourAngle)
            val rad = Math.toRadians((hourAngle - 90f).toDouble())

            val tickStartR = radius * 0.90f
            val tickEndR = radius * 0.98f
            val x1 = cx + (tickStartR * cos(rad)).toFloat()
            val y1 = cy + (tickStartR * sin(rad)).toFloat()
            val x2 = cx + (tickEndR * cos(rad)).toFloat()
            val y2 = cy + (tickEndR * sin(rad)).toFloat()

            val majorPaint = if (inNight) majorTickPaintNight else majorTickPaintDay
            canvas.drawLine(x1, y1, x2, y2, majorPaint)

            // Numerals
            val textR = radius * 0.78f
            val tx = cx + (textR * cos(rad)).toFloat()
            val ty = cy + (textR * sin(rad)).toFloat() + (textSize / 3f)
            val tPaint = if (inNight) textPaintNight else textPaintDay
            val hourLabel = h.toString()
            canvas.drawText(hourLabel, tx, ty, tPaint)

            // Minor tick at half hour (h + 0.5)
            val halfAngle = timeToAngle(h + 0.5)
            val halfInNight = isNight(halfAngle)
            val halfRad = Math.toRadians((halfAngle - 90f).toDouble())

            val minorStartR = radius * 0.94f
            val minorEndR = radius * 0.98f
            val mx1 = cx + (minorStartR * cos(halfRad)).toFloat()
            val my1 = cy + (minorStartR * sin(halfRad)).toFloat()
            val mx2 = cx + (minorEndR * cos(halfRad)).toFloat()
            val my2 = cy + (minorEndR * sin(halfRad)).toFloat()

            val minorPaint = if (halfInNight) minorTickPaintNight else minorTickPaintDay
            canvas.drawLine(mx1, my1, mx2, my2, minorPaint)
        }

        // 4. Single needle hour hand
        val handAngle = timeToAngle(timeHourFraction)
        val handRad = Math.toRadians((handAngle - 90f).toDouble())

        // Counterweight tail
        val tailLength = radius * 0.15f
        val tailX = cx - (tailLength * cos(handRad)).toFloat()
        val tailY = cy - (tailLength * sin(handRad)).toFloat()

        // Pointer tip
        val tipLength = radius * 0.85f
        val tipX = cx + (tipLength * cos(handRad)).toFloat()
        val tipY = cy + (tipLength * sin(handRad)).toFloat()

        canvas.drawLine(tailX, tailY, tipX, tipY, handPaint)
        canvas.drawCircle(cx, cy, radius * 0.04f, centerCapPaint)
    }

    companion object {
        fun timeToAngle(hourFraction: Double): Float {
            val angle = ((hourFraction - 12.0) * 15.0) % 360.0
            val normalized = if (angle < 0) angle + 360.0 else angle
            return normalized.toFloat()
        }
    }
}
