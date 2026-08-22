package com.watch1.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class WatchClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val renderer = WatchDialRenderer()
    private val handler = Handler(Looper.getMainLooper())
    private var isAttached = false

    private var config: ClockConfig = LocationHelper.loadConfig(context)
    private var bgBitmap: Bitmap? = null

    private val uvListener: () -> Unit = {
        postInvalidate()
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            invalidate()
            if (isAttached) {
                handler.postDelayed(this, 1000L)
            }
        }
    }

    fun refreshSettings() {
        config = LocationHelper.loadConfig(context)
        bgBitmap = if (config.bgMode == BackgroundMode.CUSTOM_IMAGE) {
            BackgroundImageHelper.loadBitmap(context)
        } else {
            null
        }
        invalidate()
        try {
            WatchAppWidgetProvider.updateAllWidgets(context)
        } catch (e: Exception) {
            // Ignore in environments where widget manager is unavailable
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttached = true
        UvRepository.addListener(uvListener)
        refreshSettings()
        handler.post(updateRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttached = false
        UvRepository.removeListener(uvListener)
        handler.removeCallbacks(updateRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val now = LocalTime.now()
        val date = LocalDate.now()
        val hourFraction = now.hour + now.minute / 60.0 + now.second / 3600.0
        val zoneOffsetHours = ZoneId.systemDefault().rules.getOffset(java.time.Instant.now()).totalSeconds / 3600.0

        val sunTimes = SolarCalculator.calculateSunTimes(config.lat, config.lon, date, zoneOffsetHours)
        val uvData = if (config.showUv || config.showUvIndex) UvRepository.getCachedOrFallbackUv(context, config.lat, config.lon, date) else null

        renderer.draw(
            canvas = canvas,
            width = width,
            height = height,
            timeHourFraction = hourFraction,
            sunTimes = sunTimes,
            theme = config.theme,
            showUv = config.showUv,
            showDate = config.showDate,
            showUvIndex = config.showUvIndex,
            date = date,
            uvData = uvData,
            numeralStyle = config.numeralStyle,
            numeralOrientation = config.numeralOrientation,
            numeralDisplayMode = config.numeralDisplayMode,
            fontSizeScale = config.fontSizeScale,
            numeralFont = config.numeralFont,
            handStyle = config.handStyle,
            bgMode = config.bgMode,
            customColor = config.customColor,
            bgBitmap = bgBitmap,
            isWallpaper = false,
            bezelStyle = config.bezelStyle,
            showBrandLogo = config.showBrandLogo,
            gradientDayNight = config.gradientDayNight,
            showMoonPhase = config.showMoonPhase,
            showGoldenHour = config.showGoldenHour,
            showSolarNoon = config.showSolarNoon,
            redNightMode = config.redNightMode
        )
    }
}
