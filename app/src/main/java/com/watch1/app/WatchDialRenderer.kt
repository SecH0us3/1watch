package com.watch1.app

import android.graphics.*
import java.time.LocalDate
import kotlin.math.*

class WatchDialRenderer {
    companion object {
        fun timeToAngle(hourFraction: Double): Float {
            // 12:00 is top (0 deg). 24h = 360 deg -> 15 deg per hour.
            val diff = (hourFraction - 12.0).mod(24.0)
            return (diff * 15.0).toFloat()
        }

        fun getUvColor(uvVal: Float): Int {
            return when {
                uvVal >= 11.5f -> 0xFF6A1B9A.toInt() // 11.5+ (Deep UV Violet)
                uvVal >= 10.5f -> 0xFF9C27B0.toInt() // 10.5..11.4 (Amethyst Purple)
                uvVal >= 9.5f  -> 0xFFD81B60.toInt() // 9.5..10.4 (Ruby Magenta)
                uvVal >= 8.5f  -> 0xFFE53935.toInt() // 8.5..9.4 (Crimson Red)
                uvVal >= 7.5f  -> 0xFFFF5722.toInt() // 7.5..8.4 (Flame Coral)
                uvVal >= 6.5f  -> 0xFFFB8C00.toInt() // 6.5..7.4 (Tangerine Orange)
                uvVal >= 5.5f  -> 0xFFFFB300.toInt() // 5.5..6.4 (Honey Amber)
                uvVal >= 4.5f  -> 0xFFFDD835.toInt() // 4.5..5.4 (Amber Gold)
                uvVal >= 3.5f  -> 0xFFFFEB3B.toInt() // 3.5..4.4 (Lemon Yellow)
                uvVal >= 2.5f  -> 0xFFCDDC39.toInt() // 2.5..3.4 (Chartreuse)
                uvVal >= 1.5f  -> 0xFF8BC34A.toInt() // 1.5..2.4 (Lime)
                uvVal >= 0.5f  -> 0xFF4CAF50.toInt() // 0.5..1.4 (Emerald Green)
                else -> 0
            }
        }

        private val ARABIC_LABELS = Array(24) { String.format("%02d", it) }
        private val ROMAN_LABELS = Array(24) { NumeralStyle.toRoman(it) }

        data class GradientStops(val colors: IntArray, val positions: FloatArray)

        fun interpolateColor(c1: Int, c2: Int, ratio: Float): Int {
            val r = ratio.coerceIn(0f, 1f)
            val a1 = (c1 ushr 24) and 0xFF
            val r1 = (c1 ushr 16) and 0xFF
            val g1 = (c1 ushr 8) and 0xFF
            val b1 = c1 and 0xFF

            val a2 = (c2 ushr 24) and 0xFF
            val r2 = (c2 ushr 16) and 0xFF
            val g2 = (c2 ushr 8) and 0xFF
            val b2 = c2 and 0xFF

            val a = (a1 + (a2 - a1) * r).roundToInt()
            val red = (r1 + (r2 - r1) * r).roundToInt()
            val green = (g1 + (g2 - g1) * r).roundToInt()
            val blue = (b1 + (b2 - b1) * r).roundToInt()

            return (a shl 24) or (red shl 16) or (green shl 8) or blue
        }

        fun calculateGradientStops(
            sunsetHour: Double,
            sunriseHour: Double,
            dayColor: Int,
            nightColor: Int
        ): GradientStops {
            val sunsetAngle = (timeToAngle(sunsetHour) - 90f).mod(360f)
            val sunriseAngle = (timeToAngle(sunriseHour) - 90f).mod(360f)
            val delta = 11.25f // 45 min twilight transition half-width (1.5h total)

            val nightSpan = (sunriseAngle - sunsetAngle).mod(360f)

            fun colorAt(angle: Float): Int {
                val a = angle.mod(360f)

                // Check sunset twilight
                val sunsetDiff = (a - sunsetAngle + 180f).mod(360f) - 180f
                if (abs(sunsetDiff) <= delta) {
                    val t = (sunsetDiff + delta) / (2f * delta)
                    return interpolateColor(dayColor, nightColor, t)
                }

                // Check sunrise twilight
                val sunriseDiff = (a - sunriseAngle + 180f).mod(360f) - 180f
                if (abs(sunriseDiff) <= delta) {
                    val t = (sunriseDiff + delta) / (2f * delta)
                    return interpolateColor(nightColor, dayColor, t)
                }

                val fromSunset = (a - sunsetAngle).mod(360f)
                return if (fromSunset < nightSpan) nightColor else dayColor
            }

            val rawAngles = mutableListOf(
                0f,
                (sunsetAngle - delta).mod(360f),
                sunsetAngle,
                (sunsetAngle + delta).mod(360f),
                (sunriseAngle - delta).mod(360f),
                sunriseAngle,
                (sunriseAngle + delta).mod(360f),
                360f
            )
            rawAngles.sort()

            val distinctAngles = mutableListOf<Float>()
            for (ang in rawAngles) {
                if (distinctAngles.isEmpty() || abs(ang - distinctAngles.last()) > 0.01f) {
                    distinctAngles.add(ang)
                }
            }
            if (distinctAngles.first() > 0.001f) {
                distinctAngles.add(0, 0f)
            }
            if (distinctAngles.last() < 359.999f) {
                distinctAngles.add(360f)
            }

            val colors = IntArray(distinctAngles.size) { colorAt(distinctAngles[it]) }
            val positions = FloatArray(distinctAngles.size) { (distinctAngles[it] / 360f).coerceIn(0f, 1f) }

            return GradientStops(colors, positions)
        }
    }

    private val dialBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
    }

    private val pivotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val spinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33FFFFFF")
        style = Paint.Style.FILL
    }

    private val uvArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }

    private val imageOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40000000") // 25% dark tint over photo for legibility
        style = Paint.Style.FILL
    }

    private val bezelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val bezelChamferPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    // Zero-allocation reusable geometry buffers
    private val dialRect = RectF()
    private val uvRect = RectF()
    private val srcRect = Rect()
    private val destRect = Rect()
    private val handPath = Path()
    private val handSpinePath = Path()
    private val handArrowPath = Path()
    private val dayZonePath = Path()
    private val nightZonePath = Path()
    private val complicationPillRect = RectF()

    fun draw(
        canvas: Canvas,
        width: Int,
        height: Int,
        timeHourFraction: Double,
        sunTimes: SunTimes,
        theme: DialTheme = DialTheme.CLASSIC_DARK,
        showUv: Boolean = true,
        showDate: Boolean = true,
        showUvIndex: Boolean = true,
        date: LocalDate = LocalDate.now(),
        uvData: FloatArray? = null,
        numeralStyle: NumeralStyle = NumeralStyle.ARABIC,
        numeralOrientation: NumeralOrientation = NumeralOrientation.UPRIGHT,
        numeralDisplayMode: NumeralDisplayMode = NumeralDisplayMode.ALL,
        fontSizeScale: Float = 1.0f,
        numeralFont: NumeralFont = NumeralFont.SANS_SERIF,
        handStyle: HandStyle = HandStyle.BOTTA_NEEDLE,
        bgMode: BackgroundMode = BackgroundMode.THEME_DEFAULT,
        customColor: Int = Color.parseColor("#0A0F1D"),
        bgBitmap: Bitmap? = null,
        isWallpaper: Boolean = false,
        bezelStyle: BezelStyle = BezelStyle.NONE,
        showBrandLogo: Boolean = true,
        gradientDayNight: Boolean = false
    ) {
        dialBackgroundPaint.color = theme.dialBgColor
        dayZonePaint.color = theme.dayZoneColor
        nightZonePaint.color = theme.nightZoneColor
        tickPaint.color = theme.tickColor
        textPaint.color = theme.textColor
        textPaint.typeface = numeralFont.typeface
        handPaint.color = theme.handColor
        pivotPaint.color = theme.pivotColor

        // 1. Draw Background (only fills full canvas for full-screen wallpaper)
        if (isWallpaper) {
            if (bgMode == BackgroundMode.CUSTOM_IMAGE && bgBitmap != null) {
                srcRect.set(0, 0, bgBitmap.width, bgBitmap.height)
                destRect.set(0, 0, width, height)
                canvas.drawBitmap(bgBitmap, srcRect, destRect, null)
                canvas.drawRect(destRect, imageOverlayPaint)
            } else if (bgMode == BackgroundMode.CUSTOM_COLOR) {
                canvas.drawColor(customColor)
            } else {
                canvas.drawColor(dialBackgroundPaint.color)
            }
        }

        val cx = width / 2f
        val isPortraitWallpaper = isWallpaper && height > width * 1.25f
        val outerRadius = if (isPortraitWallpaper) width * 0.43f else min(width, height) * 0.42f
        val cy = if (isPortraitWallpaper) (height * 0.10f + outerRadius) else (height / 2f)
        val radius = if (bezelStyle != BezelStyle.NONE) outerRadius * 0.885f else outerRadius
        dialRect.set(cx - radius, cy - radius, cx + radius, cy + radius)

        // 1.5 Draw Bezel Ring if enabled
        if (bezelStyle != BezelStyle.NONE) {
            drawBezel(canvas, cx, cy, outerRadius, radius, bezelStyle)
        }

        // 2. Draw Day/Night Sectors
        dayZonePath.reset()
        nightZonePath.reset()

        if (sunTimes.isPolarNight) {
            nightZonePath.addCircle(cx, cy, radius, Path.Direction.CW)
            if (gradientDayNight) {
                canvas.drawCircle(cx, cy, radius, nightZonePaint)
            } else {
                canvas.drawPath(nightZonePath, nightZonePaint)
            }
        } else if (sunTimes.isPolarDay) {
            dayZonePath.addCircle(cx, cy, radius, Path.Direction.CW)
            if (gradientDayNight) {
                canvas.drawCircle(cx, cy, radius, dayZonePaint)
            } else {
                canvas.drawPath(dayZonePath, dayZonePaint)
            }
        } else {
            val sunsetAngle = timeToAngle(sunTimes.sunsetHour) - 90f
            val sunriseAngle = timeToAngle(sunTimes.sunriseHour) - 90f
            var sweepAngle = (sunriseAngle - sunsetAngle).mod(360f)
            if (sweepAngle <= 0) sweepAngle += 360f

            // Night Sector Arc Path
            nightZonePath.moveTo(cx, cy)
            nightZonePath.arcTo(dialRect, sunsetAngle, sweepAngle)
            nightZonePath.close()

            // Day Sector Arc Path
            dayZonePath.moveTo(cx, cy)
            dayZonePath.arcTo(dialRect, sunriseAngle, 360f - sweepAngle)
            dayZonePath.close()

            if (gradientDayNight) {
                val stops = calculateGradientStops(
                    sunTimes.sunsetHour,
                    sunTimes.sunriseHour,
                    theme.dayZoneColor,
                    theme.nightZoneColor
                )
                gradientPaint.shader = SweepGradient(cx, cy, stops.colors, stops.positions)
                canvas.drawCircle(cx, cy, radius, gradientPaint)
            } else {
                if (!dayZonePath.isEmpty) {
                    canvas.drawPath(dayZonePath, dayZonePaint)
                }
                if (!nightZonePath.isEmpty) {
                    canvas.drawPath(nightZonePath, nightZonePaint)
                }
            }
        }

        // 3. Draw UV Activity Arc on Daytime Sector if enabled
        if (showUv && uvData != null && uvData.size >= 24 && !sunTimes.isPolarNight) {
            val uvArcRadius = radius * 0.94f
            uvRect.set(cx - uvArcRadius, cy - uvArcRadius, cx + uvArcRadius, cy + uvArcRadius)
            uvArcPaint.strokeWidth = radius * 0.04f
            uvArcPaint.style = Paint.Style.STROKE

            for (h in 0 until 24) {
                val uvVal = uvData[h]
                val color = getUvColor(uvVal)
                if (color != Color.TRANSPARENT) {
                    val startAngle = timeToAngle(h.toDouble()) - 90f
                    uvArcPaint.color = color
                    canvas.drawArc(uvRect, startAngle, 15f, false, uvArcPaint)
                }
            }
        }

        // 4 & 5. Draw Sub-Hour Ticks and Numerals with Dual-Mask Split-Contrast
        val baseSize = when {
            numeralStyle == NumeralStyle.BINARY -> if (numeralDisplayMode == NumeralDisplayMode.ALL) radius * 0.040f else radius * 0.048f
            numeralStyle == NumeralStyle.KANJI || numeralStyle == NumeralStyle.GREEK || numeralStyle == NumeralStyle.HEBREW -> radius * 0.062f
            numeralStyle == NumeralStyle.ROMAN -> radius * 0.068f
            numeralDisplayMode == NumeralDisplayMode.ALL -> radius * 0.065f
            else -> radius * 0.082f
        }
        textPaint.textSize = baseSize * fontSizeScale

        val fontMetrics = textPaint.fontMetrics
        val textVerticalOffset = -(fontMetrics.descent + fontMetrics.ascent) / 2f
        val labelRadius = radius * 0.74f
        val isRadial = numeralOrientation == NumeralOrientation.RADIAL

        fun drawTicksAndNumerals(textColor: Int, tickColor: Int) {
            tickPaint.color = tickColor
            textPaint.color = textColor

            for (step in 0 until 96) {
                val hourFraction = step * 0.25
                val angle = timeToAngle(hourFraction) - 90f
                val rad = Math.toRadians(angle.toDouble())

                val tickLength: Float
                val strokeW: Float

                if (step % 4 == 0) {
                    val h = step / 4
                    val isEven = h % 2 == 0
                    tickLength = if (isEven) radius * 0.08f else radius * 0.06f
                    strokeW = if (isEven) radius * 0.012f else radius * 0.009f
                } else if (step % 2 == 0) {
                    tickLength = radius * 0.042f
                    strokeW = radius * 0.006f
                } else {
                    tickLength = radius * 0.024f
                    strokeW = radius * 0.004f
                }

                tickPaint.strokeWidth = strokeW
                val x1 = (cx + (radius - tickLength) * cos(rad)).toFloat()
                val y1 = (cy + (radius - tickLength) * sin(rad)).toFloat()
                val x2 = (cx + radius * cos(rad)).toFloat()
                val y2 = (cy + radius * sin(rad)).toFloat()

                canvas.drawLine(x1, y1, x2, y2, tickPaint)
            }

            for (h in 0 until 24) {
                val shouldShowLabel = when (numeralDisplayMode) {
                    NumeralDisplayMode.EVEN_ONLY -> h % 2 == 0
                    NumeralDisplayMode.ODD_ONLY -> h % 2 != 0
                    NumeralDisplayMode.ALL -> true
                }

                if (shouldShowLabel) {
                    val angle = timeToAngle(h.toDouble()) - 90f
                    val rad = Math.toRadians(angle.toDouble())
                    val labelText = numeralStyle.formatHour(h)

                    if (numeralStyle == NumeralStyle.BINARY) {
                        val tickLen = if (h % 2 == 0) radius * 0.08f else radius * 0.06f
                        val rStart = radius - tickLen - textPaint.textSize * 0.8f
                        val stepR = if (numeralDisplayMode == NumeralDisplayMode.ALL) radius * 0.038f else radius * 0.045f
                        for (i in labelText.indices) {
                            val charRadius = rStart - i * stepR
                            val cxChar = (cx + charRadius * cos(rad)).toFloat()
                            val cyChar = (cy + charRadius * sin(rad)).toFloat()
                            canvas.drawText(labelText[i].toString(), cxChar, cyChar + textVerticalOffset, textPaint)
                        }
                    } else {
                        val labelX = (cx + labelRadius * cos(rad)).toFloat()
                        val labelY = (cy + labelRadius * sin(rad)).toFloat()

                        if (isRadial) {
                            canvas.save()
                            canvas.rotate(angle + 90f, labelX, labelY)
                            canvas.drawText(labelText, labelX, labelY + textVerticalOffset, textPaint)
                            canvas.restore()
                        } else {
                            canvas.drawText(labelText, labelX, labelY + textVerticalOffset, textPaint)
                        }
                    }
                }
            }

            // Complication 1: Date (clean typography below center pivot)
            if (showDate) {
                val dateStr = "${date.dayOfMonth} ${date.month.name.take(3)}"
                val dateY = cy + radius * 0.38f

                // Date text
                val oldSize = textPaint.textSize
                val oldTracking = textPaint.letterSpacing
                textPaint.textSize = radius * 0.048f * fontSizeScale
                textPaint.letterSpacing = 0.08f
                val dMetrics = textPaint.fontMetrics
                val dOffset = -(dMetrics.descent + dMetrics.ascent) / 2f
                canvas.drawText(dateStr, cx, dateY + dOffset, textPaint)
                textPaint.textSize = oldSize
                textPaint.letterSpacing = oldTracking
            }

            // Complication 2: UV Index (clean typography above center pivot)
            if (showUvIndex) {
                val currentHour = (timeHourFraction.toInt()).mod(24)
                val currentUv = uvData?.getOrNull(currentHour) ?: 0.0f
                val uvStr = if (currentUv > 0f) "UV %.1f".format(java.util.Locale.US, currentUv) else "UV 0"
                val uvY = if (showBrandLogo) cy - radius * 0.44f else cy - radius * 0.38f

                // Colored risk dot
                val dotRadius = radius * 0.014f
                val oldSize = textPaint.textSize
                val oldTracking = textPaint.letterSpacing
                textPaint.textSize = radius * 0.048f * fontSizeScale
                textPaint.letterSpacing = 0.05f
                val uMetrics = textPaint.fontMetrics
                val uOffset = -(uMetrics.descent + uMetrics.ascent) / 2f

                val uvColor = getUvColor(currentUv)
                if (uvColor != Color.TRANSPARENT) {
                    val textW = textPaint.measureText(uvStr)
                    val dotX = cx - (textW / 2f) - dotRadius * 2.2f
                    val dotPaint = uvArcPaint
                    dotPaint.style = Paint.Style.FILL
                    dotPaint.color = uvColor
                    canvas.drawCircle(dotX, uvY, dotRadius, dotPaint)
                }

                canvas.drawText(uvStr, cx, uvY + uOffset, textPaint)
                textPaint.textSize = oldSize
                textPaint.letterSpacing = oldTracking
            }

            // Complication 3: Brand Logo ("1watch" minimal typography)
            if (showBrandLogo) {
                val logoY = if (showUvIndex) cy - radius * 0.22f else cy - radius * 0.30f
                val oldSize = textPaint.textSize
                val oldTracking = textPaint.letterSpacing
                val oldTypeface = textPaint.typeface
                textPaint.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                textPaint.textSize = radius * 0.050f * fontSizeScale
                textPaint.letterSpacing = 0.12f
                val lMetrics = textPaint.fontMetrics
                val lOffset = -(lMetrics.descent + lMetrics.ascent) / 2f
                canvas.drawText("1watch", cx, logoY + lOffset, textPaint)
                textPaint.textSize = oldSize
                textPaint.letterSpacing = oldTracking
                textPaint.typeface = oldTypeface
            }
        }

        // Pass 1: Day Sector (crisp Day colors clipped to Day Zone)
        if (!dayZonePath.isEmpty) {
            canvas.save()
            canvas.clipPath(dayZonePath)
            drawTicksAndNumerals(theme.dayTextColor, theme.dayTickColor)
            canvas.restore()
        }

        // Pass 2: Night Sector (crisp Night colors clipped to Night Zone)
        if (!nightZonePath.isEmpty) {
            canvas.save()
            canvas.clipPath(nightZonePath)
            drawTicksAndNumerals(theme.nightTextColor, theme.nightTickColor)
            canvas.restore()
        }

        // 6. Draw 24-Hour Single Hand based on selected HandStyle
        val handAngle = timeToAngle(timeHourFraction)
        val handLength = radius * 0.88f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.rotate(handAngle) // 0 deg is straight UP (-Y direction)

        handPath.reset()
        handSpinePath.reset()

        when (handStyle) {
            HandStyle.BOTTA_NEEDLE -> {
                // Precision tapered needle with counterweight
                handPaint.style = Paint.Style.FILL
                val tailLength = radius * 0.20f
                val tailWidth = radius * 0.024f
                val baseWidth = radius * 0.028f
                val needleStart = -radius * 0.65f
                val needleWidth = radius * 0.005f
                val tip = -handLength

                handPath.moveTo(0f, tailLength)
                handPath.lineTo(-tailWidth, tailLength * 0.6f)
                handPath.lineTo(-baseWidth, 0f)
                handPath.lineTo(-needleWidth, needleStart)
                handPath.lineTo(0f, tip)
                handPath.lineTo(needleWidth, needleStart)
                handPath.lineTo(baseWidth, 0f)
                handPath.lineTo(tailWidth, tailLength * 0.6f)
                handPath.close()

                canvas.drawPath(handPath, handPaint)

                // Concentric dual pivot
                canvas.drawCircle(0f, 0f, radius * 0.042f, pivotPaint)
                dialBackgroundPaint.color = theme.dialBgColor
                canvas.drawCircle(0f, 0f, radius * 0.016f, dialBackgroundPaint)
            }
            HandStyle.BAUHAUS_BATON -> {
                // Minimalist straight baton
                handPaint.style = Paint.Style.STROKE
                handPaint.strokeCap = Paint.Cap.ROUND
                handPaint.strokeWidth = radius * 0.022f

                canvas.drawLine(0f, radius * 0.16f, 0f, -handLength, handPaint)
                canvas.drawCircle(0f, 0f, radius * 0.048f, pivotPaint)
                dialBackgroundPaint.color = theme.dialBgColor
                canvas.drawCircle(0f, 0f, radius * 0.018f, dialBackgroundPaint)
            }
            HandStyle.ARROW_SPORT -> {
                // Dynamic sport arrow with bold head and chevron tail
                handPaint.style = Paint.Style.FILL
                val stemWidth = radius * 0.016f
                val arrowBase = -radius * 0.68f
                val arrowWidth = radius * 0.055f
                val tip = -radius * 0.90f
                val tail = radius * 0.18f
                val tailFin = radius * 0.03f

                handPath.moveTo(0f, tail)
                handPath.lineTo(-tailFin, tail * 0.7f)
                handPath.lineTo(-stemWidth, 0f)
                handPath.lineTo(-stemWidth, arrowBase)
                handPath.lineTo(-arrowWidth, arrowBase)
                handPath.lineTo(0f, tip)
                handPath.lineTo(arrowWidth, arrowBase)
                handPath.lineTo(stemWidth, arrowBase)
                handPath.lineTo(stemWidth, 0f)
                handPath.lineTo(tailFin, tail * 0.7f)
                handPath.close()

                canvas.drawPath(handPath, handPaint)
                canvas.drawCircle(0f, 0f, radius * 0.042f, pivotPaint)
            }
            HandStyle.SWORD_AVIO -> {
                // Aviator sword with beveled spine accent
                handPaint.style = Paint.Style.FILL
                val midY = -radius * 0.45f
                val midWidth = radius * 0.035f
                val tip = -handLength
                val tail = radius * 0.18f
                val tailWidth = radius * 0.018f

                handPath.moveTo(0f, tail)
                handPath.lineTo(-tailWidth, tail * 0.5f)
                handPath.lineTo(-midWidth * 0.4f, 0f)
                handPath.lineTo(-midWidth, midY)
                handPath.lineTo(0f, tip)
                handPath.lineTo(midWidth, midY)
                handPath.lineTo(midWidth * 0.4f, 0f)
                handPath.lineTo(tailWidth, tail * 0.5f)
                handPath.close()

                canvas.drawPath(handPath, handPaint)

                // Half-blade specular spine
                handSpinePath.moveTo(0f, tail)
                handSpinePath.lineTo(midWidth * 0.4f, 0f)
                handSpinePath.lineTo(midWidth, midY)
                handSpinePath.lineTo(0f, tip)
                handSpinePath.close()

                canvas.drawPath(handSpinePath, spinePaint)
                canvas.drawCircle(0f, 0f, radius * 0.042f, pivotPaint)
            }
            HandStyle.SKELETON_RING -> {
                // Viewing ring framing dial markings
                handPaint.style = Paint.Style.STROKE
                handPaint.strokeWidth = radius * 0.018f
                handPaint.strokeCap = Paint.Cap.ROUND

                val ringCenterY = -radius * 0.76f
                val ringRadius = radius * 0.065f
                val tip = -radius * 0.90f

                // Stem from counterweight to bottom of ring
                canvas.drawLine(0f, radius * 0.16f, 0f, ringCenterY + ringRadius, handPaint)
                // Ring
                canvas.drawCircle(0f, ringCenterY, ringRadius, handPaint)
                // Pointer tip from top of ring
                handPaint.strokeWidth = radius * 0.010f
                canvas.drawLine(0f, ringCenterY - ringRadius, 0f, tip, handPaint)

                canvas.drawCircle(0f, 0f, radius * 0.042f, pivotPaint)
            }
            HandStyle.SPIRAL_CURVE -> {
                // Avant-garde serpentine spiral wave with counterweight swirl and precision pointer
                handPaint.style = Paint.Style.STROKE
                handPaint.strokeWidth = radius * 0.022f
                handPaint.strokeCap = Paint.Cap.ROUND

                val tip = -handLength

                // 1. Counterweight spiral curl
                handPath.moveTo(0f, 0f)
                handPath.cubicTo(
                    -radius * 0.12f, radius * 0.05f,
                    -radius * 0.12f, radius * 0.18f,
                    -radius * 0.02f, radius * 0.18f
                )
                handPath.quadTo(radius * 0.04f, radius * 0.15f, radius * 0.01f, radius * 0.08f)

                // 2. Main fluid S-spiral wave body sweeping outward and aligning to top tip
                handPath.moveTo(0f, 0f)
                handPath.cubicTo(
                    radius * 0.32f, -radius * 0.22f,
                    -radius * 0.16f, -radius * 0.60f,
                    0f, tip
                )
                canvas.drawPath(handPath, handPaint)

                // 3. Sharp arrow / flame pointer at tip
                handPaint.style = Paint.Style.FILL
                handArrowPath.reset()
                handArrowPath.moveTo(0f, tip - radius * 0.035f)
                handArrowPath.lineTo(-radius * 0.032f, tip + radius * 0.025f)
                handArrowPath.lineTo(0f, tip + radius * 0.012f)
                handArrowPath.lineTo(radius * 0.032f, tip + radius * 0.025f)
                handArrowPath.close()
                canvas.drawPath(handArrowPath, handPaint)

                // 4. Concentric vortex pivot
                canvas.drawCircle(0f, 0f, radius * 0.048f, pivotPaint)
                dialBackgroundPaint.color = theme.dialBgColor
                canvas.drawCircle(0f, 0f, radius * 0.024f, dialBackgroundPaint)
                canvas.drawCircle(0f, 0f, radius * 0.012f, pivotPaint)
            }
            HandStyle.SPIRAL_VORTEX -> {
                // Archimedean expanding galaxy vortex
                handPaint.style = Paint.Style.STROKE
                handPaint.strokeWidth = radius * 0.020f
                handPaint.strokeCap = Paint.Cap.ROUND

                handPath.reset()
                val steps = 60
                val targetR = handLength
                for (i in 0..steps) {
                    val t = i / steps.toFloat()
                    val theta = (-Math.PI / 2.0 - 2.0 * Math.PI * (1.0 - t)).toFloat()
                    val currentR = targetR * (0.06f + 0.94f * (t * t))
                    val px = (currentR * cos(theta.toDouble())).toFloat()
                    val py = (currentR * sin(theta.toDouble())).toFloat()
                    if (i == 0) handPath.moveTo(px, py) else handPath.lineTo(px, py)
                }
                canvas.drawPath(handPath, handPaint)

                // Arrow head at tip
                handPaint.style = Paint.Style.FILL
                handArrowPath.reset()
                val tip = -handLength
                handArrowPath.moveTo(0f, tip - radius * 0.035f)
                handArrowPath.lineTo(-radius * 0.032f, tip + radius * 0.025f)
                handArrowPath.lineTo(0f, tip + radius * 0.012f)
                handArrowPath.lineTo(radius * 0.032f, tip + radius * 0.025f)
                handArrowPath.close()
                canvas.drawPath(handArrowPath, handPaint)

                // Vortex concentric pivot
                canvas.drawCircle(0f, 0f, radius * 0.052f, pivotPaint)
                dialBackgroundPaint.color = theme.dialBgColor
                canvas.drawCircle(0f, 0f, radius * 0.028f, dialBackgroundPaint)
                canvas.drawCircle(0f, 0f, radius * 0.014f, pivotPaint)
            }
            HandStyle.SPIRAL_DOUBLE_DNA -> {
                // Double Helix Caduceus Spiral
                handPaint.style = Paint.Style.STROKE
                handPaint.strokeWidth = radius * 0.016f
                handPaint.strokeCap = Paint.Cap.ROUND

                val tip = -handLength
                val steps = 50

                // Strand 1
                handPath.reset()
                for (i in 0..steps) {
                    val t = i / steps.toFloat()
                    val y = t * tip
                    val waveAmp = (radius * 0.085f * (1.0f - (t - 0.5f) * (t - 0.5f) * 3f)).coerceAtLeast(radius * 0.01f)
                    val x = waveAmp * sin(t * 3.0 * Math.PI).toFloat()
                    if (i == 0) handPath.moveTo(x, y) else handPath.lineTo(x, y)
                }
                canvas.drawPath(handPath, handPaint)

                // Strand 2
                handPath.reset()
                for (i in 0..steps) {
                    val t = i / steps.toFloat()
                    val y = t * tip
                    val waveAmp = (radius * 0.085f * (1.0f - (t - 0.5f) * (t - 0.5f) * 3f)).coerceAtLeast(radius * 0.01f)
                    val x = -waveAmp * sin(t * 3.0 * Math.PI).toFloat()
                    if (i == 0) handPath.moveTo(x, y) else handPath.lineTo(x, y)
                }
                canvas.drawPath(handPath, handPaint)

                // Diamond head
                handPaint.style = Paint.Style.FILL
                handArrowPath.reset()
                handArrowPath.moveTo(0f, tip - radius * 0.035f)
                handArrowPath.lineTo(-radius * 0.026f, tip)
                handArrowPath.lineTo(0f, tip + radius * 0.035f)
                handArrowPath.lineTo(radius * 0.026f, tip)
                handArrowPath.close()
                canvas.drawPath(handArrowPath, handPaint)

                // Counterweight tail ring
                canvas.drawCircle(0f, radius * 0.14f, radius * 0.032f, handPaint)
                canvas.drawCircle(0f, 0f, radius * 0.046f, pivotPaint)
            }
            HandStyle.SPIRAL_FLAME -> {
                // Curved flame / scimitar blade with aerodynamic cutout
                handPaint.style = Paint.Style.FILL
                val tip = -handLength
                val tail = radius * 0.18f

                handPath.reset()
                handPath.moveTo(0f, tail)
                handPath.cubicTo(
                    -radius * 0.08f, tail * 0.5f,
                    -radius * 0.16f, -radius * 0.30f,
                    -radius * 0.08f, -radius * 0.65f
                )
                handPath.quadTo(-radius * 0.02f, -radius * 0.80f, 0f, tip)
                handPath.quadTo(radius * 0.12f, -radius * 0.70f, radius * 0.07f, -radius * 0.40f)
                handPath.cubicTo(
                    radius * 0.05f, -radius * 0.15f,
                    radius * 0.05f, 0f,
                    0f, tail
                )
                handPath.close()
                canvas.drawPath(handPath, handPaint)

                // Inner flame cutout
                handSpinePath.reset()
                handSpinePath.moveTo(0f, -radius * 0.12f)
                handSpinePath.quadTo(-radius * 0.07f, -radius * 0.42f, 0f, -radius * 0.68f)
                handSpinePath.quadTo(radius * 0.03f, -radius * 0.42f, 0f, -radius * 0.12f)
                handSpinePath.close()
                dialBackgroundPaint.color = theme.dialBgColor
                canvas.drawPath(handSpinePath, dialBackgroundPaint)

                canvas.drawCircle(0f, 0f, radius * 0.044f, pivotPaint)
            }
        }

        canvas.restore()
    }

    private fun drawBezel(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        outerRadius: Float,
        innerRadius: Float,
        style: BezelStyle
    ) {
        if (style == BezelStyle.NONE) return
        val ringWidth = outerRadius - innerRadius
        val midRadius = (outerRadius + innerRadius) / 2f

        val (colors, positions) = when (style) {
            BezelStyle.TITANIUM_BRUSHED -> Pair(
                intArrayOf(
                    0xFFD6DCE2.toInt(),
                    0xFF8B97A4.toInt(),
                    0xFFF2F6FA.toInt(),
                    0xFF7B8795.toInt(),
                    0xFFD0D8E0.toInt()
                ),
                floatArrayOf(0.0f, 0.25f, 0.50f, 0.75f, 1.0f)
            )
            BezelStyle.BLACK_CERAMIC -> Pair(
                intArrayOf(
                    0xFF323842.toInt(),
                    0xFF121418.toInt(),
                    0xFF3C434E.toInt(),
                    0xFF181A20.toInt(),
                    0xFF282E38.toInt()
                ),
                floatArrayOf(0.0f, 0.25f, 0.50f, 0.75f, 1.0f)
            )
            BezelStyle.POLISHED_GOLD -> Pair(
                intArrayOf(
                    0xFFF3E3B6.toInt(),
                    0xFFBA964C.toInt(),
                    0xFFFFF6DA.toInt(),
                    0xFFA17C30.toInt(),
                    0xFFEBD498.toInt()
                ),
                floatArrayOf(0.0f, 0.25f, 0.50f, 0.75f, 1.0f)
            )
            BezelStyle.NONE -> return
        }

        // 1. Base metallic ring with directional gradient
        bezelPaint.shader = LinearGradient(
            cx - outerRadius, cy - outerRadius,
            cx + outerRadius, cy + outerRadius,
            colors, positions, Shader.TileMode.CLAMP
        )
        bezelPaint.strokeWidth = ringWidth
        canvas.drawCircle(cx, cy, midRadius, bezelPaint)

        // 2. Outer rim chamfer highlight & shadow
        bezelChamferPaint.shader = null
        bezelChamferPaint.strokeWidth = outerRadius * 0.008f
        bezelChamferPaint.color = 0x66FFFFFF.toInt()
        canvas.drawCircle(cx, cy, outerRadius - bezelChamferPaint.strokeWidth / 2f, bezelChamferPaint)

        // 3. Inner step groove shadow & specular rim
        bezelChamferPaint.strokeWidth = innerRadius * 0.012f
        bezelChamferPaint.color = 0x55000000.toInt()
        canvas.drawCircle(cx, cy, innerRadius - bezelChamferPaint.strokeWidth / 2f, bezelChamferPaint)

        bezelChamferPaint.strokeWidth = innerRadius * 0.006f
        bezelChamferPaint.color = 0x44FFFFFF.toInt()
        canvas.drawCircle(cx, cy, innerRadius + bezelChamferPaint.strokeWidth / 2f, bezelChamferPaint)
    }
}
