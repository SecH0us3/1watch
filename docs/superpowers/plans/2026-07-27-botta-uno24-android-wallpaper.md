# Botta UNO 24 Android Live Wallpaper Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a native Android Live Wallpaper app on Kotlin featuring a 24-hour single-hand Botta UNO 24 style clock with an offline dynamic dark night arc computed via NOAA solar algorithm.

**Architecture:** A lightweight Android `WallpaperService` rendering to `Canvas` via a decoupled `Uno24DialRenderer`. Solar times are calculated offline by `SolarCalculator` using lat/lon coordinates from `LocationHelper`. 

**Tech Stack:** Kotlin, Android SDK 34 (min SDK 26), Gradle Kotlin DSL (`build.gradle.kts`), Android `WallpaperService`, `Canvas`, JUnit 4.

## Global Constraints
- Target SDK: 34 (Android 14) / Min SDK: 26 (Android 8.0)
- Single hour hand completing 1 revolution per 24 hours (15°/hour).
- 12:00 (Noon) at 0° top, 00:00 (Midnight) at 180° bottom.
- Dynamic Night Arc drawn from calculated Sunset to Sunrise spanning bottom half.
- Offline NOAA Solar calculation. Zero network dependencies.

---

### Task 1: Solar Calculator Math Engine (`SolarCalculator.kt`)

**Files:**
- Create: `app/src/main/java/com/botta/uno24/SolarCalculator.kt`
- Create: `app/src/test/java/com/botta/uno24/SolarCalculatorTest.kt`

**Interfaces:**
- Produces: `SolarCalculator.calculateSunTimes(lat: Double, lon: Double, date: LocalDate, timeZoneOffsetHours: Double): SunTimes`
- Data class: `data class SunTimes(val sunriseHour: Double, val sunsetHour: Double)` (fractional hours from 0.0 to 24.0).

- [ ] **Step 1: Write unit tests for NOAA solar calculator**

```kotlin
package com.botta.uno24

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class SolarCalculatorTest {
    @Test
    fun testEquinoxSolarTimesAtEquator() {
        // Equinox at equator: Sunrise approx 6.0, Sunset approx 18.0
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 0.0,
            longitude = 0.0,
            date = LocalDate.of(2026, 3, 20),
            timeZoneOffsetHours = 0.0
        )
        assertEquals(6.0, sunTimes.sunriseHour, 0.5)
        assertEquals(18.0, sunTimes.sunsetHour, 0.5)
    }
}
```

- [ ] **Step 2: Implement SolarCalculator with NOAA algorithm**

```kotlin
package com.botta.uno24

import java.time.LocalDate
import java.time.Year
import kotlin.math.*

data class SunTimes(val sunriseHour: Double, val sunsetHour: Double)

object SolarCalculator {
    fun calculateSunTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        timeZoneOffsetHours: Double
    ): SunTimes {
        val dayOfYear = date.dayOfYear
        val gamma = 2.0 * PI / 365.0 * (dayOfYear - 1 + (12.0 - 12.0) / 24.0)
        
        // Equation of time (minutes)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) - 0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))
        
        // Solar declination (radians)
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) - 0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma) - 0.002697 * cos(3 * gamma) + 0.00148 * sin(3 * gamma)

        val latRad = Math.toRadians(latitude)
        // 90.833° zenith for official sunrise/sunset (90° + 50' refraction)
        val zenith = Math.toRadians(90.833)
        
        val cosHourAngle = (cos(zenith) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
        
        // Clamp to [-1, 1] for extreme latitudes (midnight sun / polar night)
        val clampedCosHA = cosHourAngle.coerceIn(-1.0, 1.0)
        val hourAngle = Math.toDegrees(acos(clampedCosHA))

        val sunriseMinutes = 720.0 - 4.0 * (longitude + hourAngle) - eqTime + (timeZoneOffsetHours * 60.0)
        val sunsetMinutes = 720.0 - 4.0 * (longitude - hourAngle) - eqTime + (timeZoneOffsetHours * 60.0)

        val sunriseHour = (sunriseMinutes / 60.0).mod(24.0)
        val sunsetHour = (sunsetMinutes / 60.0).mod(24.0)

        return SunTimes(sunriseHour, sunsetHour)
    }
}
```

- [ ] **Step 3: Run unit tests and verify pass**

```bash
rtk ./gradlew test
```

- [ ] **Step 4: Commit Task 1**

```bash
rtk git add app/src/main/java/com/botta/uno24/SolarCalculator.kt app/src/test/java/com/botta/uno24/SolarCalculatorTest.kt
rtk git commit -m "feat: implement NOAA SolarCalculator and unit tests"
```

---

### Task 2: 24-Hour Dial Renderer Canvas (`Uno24DialRenderer.kt`)

**Files:**
- Create: `app/src/main/java/com/botta/uno24/Uno24DialRenderer.kt`
- Create: `app/src/test/java/com/botta/uno24/Uno24DialRendererTest.kt`

**Interfaces:**
- Consumes: `SunTimes` from `SolarCalculator`.
- Produces: `Uno24DialRenderer.draw(canvas: Canvas, width: Int, height: Int, timeHourFraction: Double, sunTimes: SunTimes)`
- Geometry math: `timeToAngle(hourFraction: Double): Float` ($0^\circ$ at 12:00 noon, $180^\circ$ at 00:00 midnight).

- [ ] **Step 1: Write angle calculation tests for Uno24DialRenderer**

```kotlin
package com.botta.uno24

import org.junit.Assert.assertEquals
import org.junit.Test

class Uno24DialRendererTest {
    @Test
    fun testTimeToAngle() {
        // 12:00 Noon -> 0 degrees
        assertEquals(0f, Uno24DialRenderer.timeToAngle(12.0), 0.01f)
        // 18:00 Evening -> 90 degrees
        assertEquals(90f, Uno24DialRenderer.timeToAngle(18.0), 0.01f)
        // 00:00 Midnight -> 180 degrees
        assertEquals(180f, Uno24DialRenderer.timeToAngle(0.0), 0.01f)
        // 06:00 Morning -> 270 degrees
        assertEquals(270f, Uno24DialRenderer.timeToAngle(6.0), 0.01f)
    }
}
```

- [ ] **Step 2: Implement Uno24DialRenderer**

```kotlin
package com.botta.uno24

import android.graphics.*
import kotlin.math.*

class Uno24DialRenderer {
    companion object {
        fun timeToAngle(hourFraction: Double): Float {
            // 12:00 is top (0 deg). 24h = 360 deg -> 15 deg per hour.
            // hourFraction relative to 12.0:
            val diff = (hourFraction - 12.0).mod(24.0)
            return (diff * 15.0).toFloat()
        }
    }

    private val dialBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#121212")
        style = Paint.Style.FILL
    }
    
    private val dayZonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E1E1E")
        style = Paint.Style.FILL
    }

    private val nightZonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#08080C")
        style = Paint.Style.FILL
    }

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0E0E0")
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCCCCC")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    private val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF3D00") // Botta orange/red single hand
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    fun draw(
        canvas: Canvas,
        width: Int,
        height: Int,
        timeHourFraction: Double,
        sunTimes: SunTimes
    ) {
        canvas.drawColor(dialBackgroundPaint.color)

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) * 0.42f
        val dialRect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        // 1. Draw Day Base
        canvas.drawCircle(cx, cy, radius, dayZonePaint)

        // 2. Draw Night Arc (From Sunset angle to Sunrise angle)
        // Note: Canvas drawArc 0 deg is at 3 o'clock (18:00). 
        // Our timeToAngle: 12:00 = -90 canvas deg, 18:00 = 0 canvas deg.
        val sunsetAngle = timeToAngle(sunTimes.sunsetHour) - 90f
        val sunriseAngle = timeToAngle(sunTimes.sunriseHour) - 90f
        var sweepAngle = (sunriseAngle - sunsetAngle).mod(360f)
        if (sweepAngle <= 0) sweepAngle += 360f

        canvas.drawArc(dialRect, sunsetAngle, sweepAngle, true, nightZonePaint)

        // 3. Draw Hour Markings & Numbers (0 to 23)
        textPaint.textSize = radius * 0.08f
        for (h in 0 until 24) {
            val angle = timeToAngle(h.toDouble()) - 90f
            val rad = Math.toRadians(angle.toDouble())

            val isMajor = h % 2 == 0
            val tickLength = if (isMajor) radius * 0.08f else radius * 0.04f
            tickPaint.strokeWidth = if (isMajor) radius * 0.012f else radius * 0.006f

            val x1 = (cx + (radius - tickLength) * cos(rad)).toFloat()
            val y1 = (cy + (radius - tickLength) * sin(rad)).toFloat()
            val x2 = (cx + radius * cos(rad)).toFloat()
            val y2 = (cy + radius * sin(rad)).toFloat()

            canvas.drawLine(x1, y1, x2, y2, tickPaint)

            if (isMajor) {
                val labelX = (cx + (radius - tickLength - textPaint.textSize * 0.8f) * cos(rad)).toFloat()
                val labelY = (cy + (radius - tickLength - textPaint.textSize * 0.8f) * sin(rad) + textPaint.textSize * 0.35f).toFloat()
                canvas.drawText(String.format("%02d", h), labelX, labelY, textPaint)
            }
        }

        // 4. Draw Single Hour Hand
        val handAngle = timeToAngle(timeHourFraction) - 90f
        val handRad = Math.toRadians(handAngle.toDouble())
        val handLength = radius * 0.88f

        handPaint.strokeWidth = radius * 0.025f
        val hx = (cx + handLength * cos(handRad)).toFloat()
        val hy = (cy + handLength * sin(handRad)).toFloat()

        canvas.drawLine(cx, cy, hx, hy, handPaint)
        canvas.drawCircle(cx, cy, radius * 0.04f, handPaint.apply { style = Paint.Style.FILL })
    }
}
```

- [ ] **Step 3: Run dial renderer tests**

```bash
rtk ./gradlew test
```

- [ ] **Step 4: Commit Task 2**

```bash
rtk git add app/src/main/java/com/botta/uno24/Uno24DialRenderer.kt app/src/test/java/com/botta/uno24/Uno24DialRendererTest.kt
rtk git commit -m "feat: implement Uno24DialRenderer for 24h canvas clock drawing"
```

---

### Task 3: Android Live Wallpaper Service & Engine (`Uno24WallpaperService.kt`)

**Files:**
- Create: `app/src/main/java/com/botta/uno24/Uno24WallpaperService.kt`
- Create: `app/src/main/res/xml/wallpaper.xml`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create wallpaper xml resource**

```xml
<?xml version="1.0" encoding="utf-8"?>
<wallpaper xmlns:android="http://schemas.android.com/apk/res/android"
    android:thumbnail="@mipmap/ic_launcher"
    android:description="@string/wallpaper_description" />
```

- [ ] **Step 2: Implement Uno24WallpaperService**

```kotlin
package com.botta.uno24

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
                    // Schedule next redraw in 1 second or 10 seconds for battery saving
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

        private fun drawFrame() {
            val holder = surfaceHolder ?: return
            val canvas = holder.lockCanvas() ?: return
            try {
                val now = LocalTime.now()
                val date = LocalDate.now()
                val hourFraction = now.hour + now.minute / 60.0 + now.second / 3600.0

                val zoneOffsetHours = ZoneId.systemDefault().rules.getOffset(java.time.Instant.now()).totalSeconds / 3600.0

                // Default location (e.g. Berlin / Europe offset) if no GPS yet
                val lat = 52.52
                val lon = 13.405

                val sunTimes = SolarCalculator.calculateSunTimes(lat, lon, date, zoneOffsetHours)

                renderer.draw(canvas, canvas.width, canvas.height, hourFraction, sunTimes)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
```

- [ ] **Step 3: Declare Wallpaper Service in AndroidManifest.xml**

```xml
<service
    android:name=".Uno24WallpaperService"
    android:label="Botta UNO 24 Clock"
    android:permission="android.permission.BIND_WALLPAPER"
    android:exported="true">
    <intent-filter>
        <action android:name="android.service.wallpaper.WallpaperService" />
    </intent-filter>
    <meta-data
        android:name="android.service.wallpaper"
        android:resource="@xml/wallpaper" />
</service>
```

- [ ] **Step 4: Commit Task 3**

```bash
rtk git add app/src/main/java/com/botta/uno24/Uno24WallpaperService.kt app/src/main/res/xml/wallpaper.xml app/src/main/AndroidManifest.xml
rtk git commit -m "feat: implement Uno24WallpaperService for Android Live Wallpaper engine"
```

---

### Task 4: Main Activity & Wallpaper Launcher (`MainActivity.kt`)

**Files:**
- Create: `app/src/main/java/com/botta/uno24/MainActivity.kt`
- Create: `app/src/main/res/layout/activity_main.xml`

- [ ] **Step 1: Implement Main Activity with Set Wallpaper Button**

```kotlin
package com.botta.uno24

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnSetWallpaper).setOnClickListener {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@MainActivity, Uno24WallpaperService::class.java)
                )
            }
            startActivity(intent)
        }
    }
}
```

- [ ] **Step 2: Commit Task 4**

```bash
rtk git add app/src/main/java/com/botta/uno24/MainActivity.kt app/src/main/res/layout/activity_main.xml
rtk git commit -m "feat: implement MainActivity launcher for live wallpaper setup"
```

---

### Task 5: Project Gradle Configuration & Build Verification

**Files:**
- Create: `build.gradle.kts`
- Create: `settings.gradle.kts`
- Create: `app/build.gradle.kts`

- [ ] **Step 1: Create Android Gradle project files**
- [ ] **Step 2: Run Gradle assemble and test verification**

```bash
rtk ./gradlew test assembleDebug
```

- [ ] **Step 3: Commit Task 5**

```bash
rtk git add .
rtk git commit -m "build: configure Android Gradle build setup and verify compilation"
```
