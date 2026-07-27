package com.uno24.wallpaper

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

    inner class Uno24Engine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val renderer = Uno24DialRenderer()
        private var visible = false

        private val gestureDetector = GestureDetector(this@Uno24WallpaperService, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY) && abs(diffX) > 100 && abs(velocityX) > 100) {
                    val currentTheme = LocationHelper.getSavedTheme(this@Uno24WallpaperService)
                    val newTheme = if (diffX < 0) currentTheme.next() else currentTheme.previous()
                    LocationHelper.saveTheme(this@Uno24WallpaperService, newTheme)
                    drawFrame()
                    return true
                }
                return false
            }
        })

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
        }

        private val drawRunnable = object : Runnable {
            override fun run() {
                drawFrame()
                if (visible) {
                    handler.postDelayed(this, 1000L)
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunnable)
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
        }

        private fun drawFrame() {
            val holder = surfaceHolder ?: return
            val canvas = holder.lockCanvas() ?: return
            try {
                val now = LocalTime.now()
                val date = LocalDate.now()
                val hourFraction = now.hour + now.minute / 60.0 + now.second / 3600.0

                val zoneOffsetHours = ZoneId.systemDefault().rules.getOffset(java.time.Instant.now()).totalSeconds / 3600.0

                val (lat, lon) = LocationHelper.getSavedCoordinates(this@Uno24WallpaperService)
                val currentTheme = LocationHelper.getSavedTheme(this@Uno24WallpaperService)
                val showUv = LocationHelper.getShowUv(this@Uno24WallpaperService)

                val sunTimes = SolarCalculator.calculateSunTimes(lat, lon, date, zoneOffsetHours)
                val uvData = if (showUv) UvRepository.getCachedOrFallbackUv(this@Uno24WallpaperService, lat, lon, date) else null

                renderer.draw(
                    canvas = canvas,
                    width = canvas.width,
                    height = canvas.height,
                    timeHourFraction = hourFraction,
                    sunTimes = sunTimes,
                    theme = currentTheme,
                    showUv = showUv,
                    uvData = uvData
                )
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
