package com.uno24.wallpaper

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class Uno24ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val renderer = Uno24DialRenderer()
    private val handler = Handler(Looper.getMainLooper())
    private var isAttached = false

    private val updateRunnable = object : Runnable {
        override fun run() {
            invalidate()
            if (isAttached) {
                handler.postDelayed(this, 1000L)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttached = true
        handler.post(updateRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttached = false
        handler.removeCallbacks(updateRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val now = LocalTime.now()
        val date = LocalDate.now()
        val hourFraction = now.hour + now.minute / 60.0 + now.second / 3600.0
        val zoneOffsetHours = ZoneId.systemDefault().rules.getOffset(java.time.Instant.now()).totalSeconds / 3600.0

        val (lat, lon) = LocationHelper.getSavedCoordinates(context)
        val currentTheme = LocationHelper.getSavedTheme(context)
        val showUv = LocationHelper.getShowUv(context)
        val numeralStyle = LocationHelper.getNumeralStyle(context)
        val numeralOrientation = LocationHelper.getNumeralOrientation(context)
        val numeralDisplayMode = LocationHelper.getNumeralDisplayMode(context)

        val sunTimes = SolarCalculator.calculateSunTimes(lat, lon, date, zoneOffsetHours)
        val uvData = if (showUv) UvRepository.getCachedOrFallbackUv(context, lat, lon, date) else null

        renderer.draw(
            canvas = canvas,
            width = width,
            height = height,
            timeHourFraction = hourFraction,
            sunTimes = sunTimes,
            theme = currentTheme,
            showUv = showUv,
            uvData = uvData,
            numeralStyle = numeralStyle,
            numeralOrientation = numeralOrientation,
            numeralDisplayMode = numeralDisplayMode
        )
    }
}
