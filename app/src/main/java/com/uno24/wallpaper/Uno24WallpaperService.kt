package com.uno24.wallpaper

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.abs

class Uno24WallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = Uno24Engine()

    inner class Uno24Engine : Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        private val handler = Handler(Looper.getMainLooper())
        private val renderer = Uno24DialRenderer()
        private var visible = false

        private var config: ClockConfig = LocationHelper.loadConfig(this@Uno24WallpaperService)
        private var bgBitmap: Bitmap? = null

        private val gestureDetector = GestureDetector(this@Uno24WallpaperService, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY) && abs(diffX) > 100 && abs(velocityX) > 100) {
                    val currentTheme = config.theme
                    val newTheme = if (diffX < 0) currentTheme.next() else currentTheme.previous()
                    LocationHelper.saveTheme(this@Uno24WallpaperService, newTheme)
                    // The shared preferences listener will reload config and trigger drawFrame()
                    return true
                }
                return false
            }
        })

        private val uvListener: () -> Unit = {
            if (visible) {
                drawFrame()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
            reloadConfig()
            LocationHelper.getPrefs(this@Uno24WallpaperService).registerOnSharedPreferenceChangeListener(this)
            UvRepository.addListener(uvListener)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            reloadConfig()
            if (visible) {
                drawFrame()
            }
        }

        private fun reloadConfig() {
            config = LocationHelper.loadConfig(this@Uno24WallpaperService)
            bgBitmap = if (config.bgMode == BackgroundMode.CUSTOM_IMAGE) {
                BackgroundImageHelper.loadBitmap(this@Uno24WallpaperService)
            } else {
                null
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
        }

        private val drawRunnable = object : Runnable {
            override fun run() {
                drawFrame()
                if (visible) {
                    // Power-efficient frame scheduling: align to the next 15-second boundary
                    val nowMs = System.currentTimeMillis()
                    val delayMs = (15000L - (nowMs % 15000L)).coerceIn(500L, 15000L)
                    handler.postDelayed(this, delayMs)
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                reloadConfig()
                drawFrame()
                handler.removeCallbacks(drawRunnable)
                val nowMs = System.currentTimeMillis()
                val delayMs = (15000L - (nowMs % 15000L)).coerceIn(500L, 15000L)
                handler.postDelayed(drawRunnable, delayMs)
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunnable)
        }

        override fun onDestroy() {
            super.onDestroy()
            visible = false
            handler.removeCallbacks(drawRunnable)
            LocationHelper.getPrefs(this@Uno24WallpaperService).unregisterOnSharedPreferenceChangeListener(this)
            UvRepository.removeListener(uvListener)
        }

        private fun drawFrame() {
            val holder = surfaceHolder ?: return
            val canvas = holder.lockCanvas() ?: return
            try {
                val now = LocalTime.now()
                val date = LocalDate.now()
                val hourFraction = now.hour + now.minute / 60.0 + now.second / 3600.0
                val zoneOffsetHours = ZoneId.systemDefault().rules.getOffset(java.time.Instant.now()).totalSeconds / 3600.0

                val sunTimes = SolarCalculator.calculateSunTimes(config.lat, config.lon, date, zoneOffsetHours)
                val uvData = if (config.showUv || config.showUvIndex) UvRepository.getCachedOrFallbackUv(this@Uno24WallpaperService, config.lat, config.lon, date) else null

                renderer.draw(
                    canvas = canvas,
                    width = canvas.width,
                    height = canvas.height,
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
                    isWallpaper = true
                )
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
