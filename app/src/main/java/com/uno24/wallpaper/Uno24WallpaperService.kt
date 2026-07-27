package com.uno24.wallpaper

import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class Uno24WallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = Uno24Engine()

    inner class Uno24Engine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val renderer = Uno24DialRenderer()
        private var visible = false

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

                val sunTimes = SolarCalculator.calculateSunTimes(lat, lon, date, zoneOffsetHours)

                renderer.draw(canvas, canvas.width, canvas.height, hourFraction, sunTimes, currentTheme)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
