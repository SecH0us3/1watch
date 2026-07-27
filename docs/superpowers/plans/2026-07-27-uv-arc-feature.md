# UV Activity Arc Feature Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Open-Meteo API integration and Canvas daytime UV arc rendering with offline solar fallback and a settings toggle in the UNO 24 Android Live Wallpaper.

**Tech Stack:** Kotlin, `HttpURLConnection` / `org.json.JSONObject`, Android `Canvas`, `SharedPreferences`.

---

### Task 1: UV Repository & Offline Solar Fallback (`UvRepository.kt`)

**Files:**
- Create: `app/src/main/java/com/uno24/wallpaper/UvRepository.kt`
- Create: `app/src/test/java/com/uno24/wallpaper/UvRepositoryTest.kt`

**Interfaces:**
- Produces: `UvRepository.getHourlyUv(context: Context, lat: Double, lon: Double, date: LocalDate): FloatArray` (24 floats for hours 0..23).
- Offline formula: `estimateUvFromSolarAngle(lat: Double, lon: Double, date: LocalDate, hour: Int): Float`.

- [ ] **Step 1: Write unit tests for UV estimation**

```kotlin
package com.uno24.wallpaper

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class UvRepositoryTest {
    @Test
    fun testEstimateUvAtNoon() {
        val uvNoon = UvRepository.estimateUvFromSolarAngle(lat = 45.0, lon = 15.0, date = LocalDate.of(2026, 6, 21), hour = 12)
        assertTrue("Noon UV should be positive", uvNoon > 3.0f)
    }

    @Test
    fun testEstimateUvAtMidnight() {
        val uvMidnight = UvRepository.estimateUvFromSolarAngle(lat = 45.0, lon = 15.0, date = LocalDate.of(2026, 6, 21), hour = 0)
        assertTrue("Midnight UV should be 0", uvMidnight == 0.0f)
    }
}
```

- [ ] **Step 2: Implement UvRepository**

```kotlin
package com.uno24.wallpaper

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.util.concurrent.Executors
import kotlin.math.*

object UvRepository {
    private const val PREFS_NAME = "Uno24UvCache"
    private const val KEY_UV_DATA = "key_uv_data"
    private const val KEY_LAST_FETCH = "key_last_fetch"
    private val executor = Executors.newSingleThreadExecutor()

    fun estimateUvFromSolarAngle(lat: Double, lon: Double, date: LocalDate, hour: Int): Float {
        val sunTimes = SolarCalculator.calculateSunTimes(lat, lon, date, 0.0)
        if (hour < sunTimes.sunriseHour || hour > sunTimes.sunsetHour) return 0f

        val noon = (sunTimes.sunriseHour + sunTimes.sunsetHour) / 2.0
        val distFromNoon = abs(hour - noon)
        val halfDay = (sunTimes.sunsetHour - sunTimes.sunriseHour) / 2.0
        if (distFromNoon >= halfDay) return 0f

        val factor = cos((distFromNoon / halfDay) * (PI / 2.0))
        return (10.0 * factor).toFloat().coerceIn(0f, 12f)
    }

    fun getCachedOrFallbackUv(context: Context, lat: Double, lon: Double, date: LocalDate): FloatArray {
        val cached = getCachedUv(context)
        if (cached != null) return cached

        val fallback = FloatArray(24)
        for (h in 0 until 24) {
            fallback[h] = estimateUvFromSolarAngle(lat, lon, date, h)
        }
        fetchAsync(context, lat, lon)
        return fallback
    }

    private fun fetchAsync(context: Context, lat: Double, lon: Double) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastFetch = prefs.getLong(KEY_LAST_FETCH, 0L)
        if (System.currentTimeMillis() - lastFetch < 3 * 3600 * 1000L) return

        executor.execute {
            try {
                val urlStr = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&hourly=uv_index&timezone=auto"
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                if (conn.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                    reader.close()

                    val json = JSONObject(sb.toString())
                    val hourly = json.optJSONObject("hourly")
                    val uvArray = hourly?.optJSONArray("uv_index")
                    if (uvArray != null && uvArray.length() >= 24) {
                        val result = FloatArray(24)
                        for (i in 0 until 24) {
                            result[i] = uvArray.optDouble(i, 0.0).toFloat()
                        }
                        saveCache(context, result)
                    }
                }
                conn.disconnect()
            } catch (e: Exception) {
                // Ignore network failure, fall back to solar estimation
            }
        }
    }

    private fun saveCache(context: Context, uvData: FloatArray) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val sb = StringBuilder()
        for (i in uvData.indices) {
            sb.append(uvData[i]).append(",")
        }
        prefs.edit()
            .putString(KEY_UV_DATA, sb.toString())
            .putLong(KEY_LAST_FETCH, System.currentTimeMillis())
            .apply()
    }

    private fun getCachedUv(context: Context): FloatArray? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val str = prefs.getString(KEY_UV_DATA, null) ?: return null
        val parts = str.split(",")
        if (parts.size < 24) return null

        val result = FloatArray(24)
        for (i in 0 until 24) {
            result[i] = parts[i].toFloatOrNull() ?: 0f
        }
        return result
    }
}
```

- [ ] **Step 3: Run unit tests**

```bash
rtk ./gradlew test
```

- [ ] **Step 4: Commit Task 1**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/UvRepository.kt app/src/test/java/com/uno24/wallpaper/UvRepositoryTest.kt
rtk git commit -m "feat: implement UvRepository with Open-Meteo API fetching and offline solar estimation"
```

---

### Task 2: Canvas Daytime UV Arc Rendering & Settings Toggle

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/LocationHelper.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24WallpaperService.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/MainActivity.kt`
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Add INTERNET permission to AndroidManifest.xml**

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

- [ ] **Step 2: Add UV toggle in LocationHelper.kt**

```kotlin
private const val KEY_SHOW_UV = "key_show_uv"

fun getShowUv(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_SHOW_UV, true)
}

fun saveShowUv(context: Context, enabled: Boolean) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_SHOW_UV, enabled).apply()
}
```

- [ ] **Step 3: Render UV Arc in Uno24DialRenderer.kt**

Add `showUv: Boolean = true, uvData: FloatArray? = null` to `Uno24DialRenderer.draw(...)`. When `showUv` is true and `uvData` is provided, iterate hours 0..23, draw a colored arc segment along daytime hours where `uv >= 3.0f` (yellow for 3-5, orange for 6-7, purple/red for 8+).

- [ ] **Step 4: Update Uno24WallpaperService.kt to fetch and pass UV data**

- [ ] **Step 5: Add Switch UI in activity_main.xml & wire in MainActivity.kt**

- [ ] **Step 6: Build verification and test**

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew assembleDebug test
```

- [ ] **Step 7: Commit Task 2**

```bash
rtk git add .
rtk git commit -m "feat: render daytime UV activity arc with Open-Meteo integration and settings toggle"
```
