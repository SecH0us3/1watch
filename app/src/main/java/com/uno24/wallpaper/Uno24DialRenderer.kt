package com.uno24.wallpaper

import android.graphics.*
import kotlin.math.*

class Uno24DialRenderer {
    companion object {
        fun timeToAngle(hourFraction: Double): Float {
            // 12:00 is top (0 deg). 24h = 360 deg -> 15 deg per hour.
            val diff = (hourFraction - 12.0).mod(24.0)
            return (diff * 15.0).toFloat()
        }
    }

    private val dialBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val dayZonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val nightZonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val pivotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val uvArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }

    fun draw(
        canvas: Canvas,
        width: Int,
        height: Int,
        timeHourFraction: Double,
        sunTimes: SunTimes,
        theme: DialTheme = DialTheme.CLASSIC_DARK,
        showUv: Boolean = true,
        uvData: FloatArray? = null,
        numeralStyle: NumeralStyle = NumeralStyle.ARABIC,
        numeralOrientation: NumeralOrientation = NumeralOrientation.UPRIGHT,
        numeralDisplayMode: NumeralDisplayMode = NumeralDisplayMode.EVEN_ONLY,
        numeralSize: NumeralSize = NumeralSize.MEDIUM,
        numeralFont: NumeralFont = NumeralFont.SANS_SERIF
    ) {
        dialBackgroundPaint.color = theme.dialBgColor
        dayZonePaint.color = theme.dayZoneColor
        nightZonePaint.color = theme.nightZoneColor
        tickPaint.color = theme.tickColor
        textPaint.color = theme.textColor
        textPaint.typeface = numeralFont.typeface
        handPaint.color = theme.handColor
        pivotPaint.color = theme.pivotColor

        canvas.drawColor(dialBackgroundPaint.color)

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) * 0.42f
        val dialRect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        // 1. Draw Day Base
        canvas.drawCircle(cx, cy, radius, dayZonePaint)

        // 2. Draw Night Arc (From Sunset angle to Sunrise angle across Midnight)
        val sunsetAngle = timeToAngle(sunTimes.sunsetHour) - 90f
        val sunriseAngle = timeToAngle(sunTimes.sunriseHour) - 90f
        var sweepAngle = (sunriseAngle - sunsetAngle).mod(360f)
        if (sweepAngle <= 0) sweepAngle += 360f

        canvas.drawArc(dialRect, sunsetAngle, sweepAngle, true, nightZonePaint)

        // 3. Draw UV Activity Arc on Daytime Sector if enabled
        if (showUv && uvData != null && uvData.size >= 24) {
            val uvArcRadius = radius * 0.94f
            val uvRect = RectF(cx - uvArcRadius, cy - uvArcRadius, cx + uvArcRadius, cy + uvArcRadius)
            uvArcPaint.strokeWidth = radius * 0.04f

            for (h in 0 until 24) {
                val uvVal = uvData[h]
                if (uvVal >= 3.0f) {
                    val startAngle = timeToAngle(h.toDouble()) - 90f
                    uvArcPaint.color = when {
                        uvVal >= 8.0f -> Color.parseColor("#D500F9") // Very High / Extreme
                        uvVal >= 6.0f -> Color.parseColor("#FF6D00") // High
                        else -> Color.parseColor("#FFD600")          // Moderate
                    }
                    canvas.drawArc(uvRect, startAngle, 15f, false, uvArcPaint)
                }
            }
        }

        // 4. Draw Hour Markings & Numbers (0 to 23)
        val baseSize = when {
            numeralDisplayMode == NumeralDisplayMode.ALL -> radius * 0.055f
            numeralStyle == NumeralStyle.ROMAN -> radius * 0.065f
            else -> radius * 0.075f
        }
        textPaint.textSize = baseSize * numeralSize.scale

        for (h in 0 until 24) {
            val angle = timeToAngle(h.toDouble()) - 90f
            val rad = Math.toRadians(angle.toDouble())

            val shouldShowLabel = when (numeralDisplayMode) {
                NumeralDisplayMode.EVEN_ONLY -> h % 2 == 0
                NumeralDisplayMode.ODD_ONLY -> h % 2 != 0
                NumeralDisplayMode.ALL -> true
            }

            val isMajor = h % 2 == 0
            val tickLength = if (isMajor) radius * 0.08f else radius * 0.04f
            tickPaint.strokeWidth = if (isMajor) radius * 0.012f else radius * 0.006f

            val x1 = (cx + (radius - tickLength) * cos(rad)).toFloat()
            val y1 = (cy + (radius - tickLength) * sin(rad)).toFloat()
            val x2 = (cx + radius * cos(rad)).toFloat()
            val y2 = (cy + radius * sin(rad)).toFloat()

            canvas.drawLine(x1, y1, x2, y2, tickPaint)

            if (shouldShowLabel) {
                val labelText = if (numeralStyle == NumeralStyle.ROMAN) NumeralStyle.toRoman(h) else String.format("%02d", h)
                val labelRadius = radius - tickLength - textPaint.textSize * 1.1f
                val labelX = (cx + labelRadius * cos(rad)).toFloat()
                val labelY = (cy + labelRadius * sin(rad)).toFloat()

                if (numeralOrientation == NumeralOrientation.RADIAL) {
                    canvas.save()
                    canvas.rotate(angle + 90f, labelX, labelY)
                    canvas.drawText(labelText, labelX, labelY + textPaint.textSize * 0.35f, textPaint)
                    canvas.restore()
                } else {
                    canvas.drawText(labelText, labelX, labelY + textPaint.textSize * 0.35f, textPaint)
                }
            }
        }

        // 5. Draw Single Hour Hand
        val handAngle = timeToAngle(timeHourFraction) - 90f
        val handRad = Math.toRadians(handAngle.toDouble())
        val handLength = radius * 0.88f

        handPaint.strokeWidth = radius * 0.025f
        val hx = (cx + handLength * cos(handRad)).toFloat()
        val hy = (cy + handLength * sin(handRad)).toFloat()

        canvas.drawLine(cx, cy, hx, hy, handPaint)
        canvas.drawCircle(cx, cy, radius * 0.04f, pivotPaint)
    }
}
