package com.botta.uno24

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.time.ZonedDateTime

class Uno24WallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return Uno24Engine()
    }

    inner class Uno24Engine : WallpaperService.Engine() {

        private val renderer = Uno24DialRenderer()
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private val frameDelayMillis = 33L

        private val drawRunnable = Runnable {
            drawFrame()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                drawFrame()
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            if (visible) {
                drawFrame()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
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
            handler.removeCallbacks(drawRunnable)
            if (!visible) return

            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    val width = canvas.width
                    val height = canvas.height

                    val now = ZonedDateTime.now()
                    val date = now.toLocalDate()
                    val offsetHours = now.offset.totalSeconds / 3600.0
                    val hourFraction = now.hour + now.minute / 60.0 + now.second / 3600.0 + now.nano / 3_600_000_000_000.0

                    val sunTimes = SolarCalculator.calculateSunTimes(
                        latitude = 52.52,
                        longitude = 13.405,
                        date = date,
                        timeZoneOffsetHours = offsetHours
                    )

                    renderer.draw(canvas, width, height, hourFraction, sunTimes)
                }
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        // Surface destroyed or invalid state
                    }
                }
            }

            if (visible) {
                handler.postDelayed(drawRunnable, frameDelayMillis)
            }
        }
    }
}
