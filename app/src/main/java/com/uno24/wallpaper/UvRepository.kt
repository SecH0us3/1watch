package com.uno24.wallpaper

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import kotlin.math.*

object UvRepository {
    private const val PREFS_NAME = "Uno24UvCache"
    private val executor = Executors.newSingleThreadExecutor()
    private val listeners = CopyOnWriteArrayList<() -> Unit>()

    fun addListener(listener: () -> Unit) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        val action = Runnable {
            listeners.forEach {
                try {
                    it.invoke()
                } catch (e: Exception) {
                    // Ignore listener error
                }
            }
        }
        try {
            val looper = Looper.getMainLooper()
            if (looper != null) {
                Handler(looper).post(action)
            } else {
                action.run()
            }
        } catch (e: Throwable) {
            action.run()
        }
    }

    fun estimateUvFromSolarAngle(lat: Double, lon: Double, date: LocalDate, hour: Int): Float {
        val localOffset = lon / 15.0
        val sunTimes = SolarCalculator.calculateSunTimes(lat, lon, date, localOffset)
        if (hour < sunTimes.sunriseHour || hour > sunTimes.sunsetHour) return 0f

        val noon = (sunTimes.sunriseHour + sunTimes.sunsetHour) / 2.0
        val distFromNoon = abs(hour - noon)
        val halfDay = (sunTimes.sunsetHour - sunTimes.sunriseHour) / 2.0
        if (distFromNoon >= halfDay) return 0f

        val factor = cos((distFromNoon / halfDay) * (PI / 2.0))
        val latRad = Math.toRadians(abs(lat))
        val peakUv = (13.0 * cos(latRad)).coerceIn(4.0, 13.0)
        return (peakUv * factor).toFloat().coerceIn(0f, 14f)
    }

    private fun getCacheKey(lat: Double, lon: Double, date: LocalDate): String {
        val latKey = "%.1f".format(java.util.Locale.US, lat)
        val lonKey = "%.1f".format(java.util.Locale.US, lon)
        return "key_uv_${latKey}_${lonKey}_${date}"
    }

    fun getCachedOrFallbackUv(context: Context, lat: Double, lon: Double, date: LocalDate): FloatArray {
        val cached = getCachedUv(context, lat, lon, date)
        if (cached != null) return cached

        val fallback = FloatArray(24)
        for (h in 0 until 24) {
            fallback[h] = estimateUvFromSolarAngle(lat, lon, date, h)
        }
        fetchAsync(context, lat, lon, date)
        return fallback
    }

    private fun fetchAsync(context: Context, lat: Double, lon: Double, date: LocalDate) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cacheKey = getCacheKey(lat, lon, date)
        val lastFetchKey = "${cacheKey}_last_fetch"
        val lastFetch = prefs.getLong(lastFetchKey, 0L)
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
                        saveCache(context, cacheKey, lastFetchKey, result)
                        notifyListeners()
                    }
                }
                conn.disconnect()
            } catch (e: Exception) {
                // Network error, fallback used
            }
        }
    }

    private fun saveCache(context: Context, cacheKey: String, lastFetchKey: String, uvData: FloatArray) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val sb = StringBuilder()
        for (i in uvData.indices) {
            sb.append(uvData[i]).append(",")
        }
        prefs.edit()
            .putString(cacheKey, sb.toString())
            .putLong(lastFetchKey, System.currentTimeMillis())
            .apply()
    }

    private fun getCachedUv(context: Context, lat: Double, lon: Double, date: LocalDate): FloatArray? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cacheKey = getCacheKey(lat, lon, date)
        val str = prefs.getString(cacheKey, null) ?: return null
        val parts = str.split(",")
        if (parts.size < 24) return null

        val result = FloatArray(24)
        for (i in 0 until 24) {
            result[i] = parts[i].toFloatOrNull() ?: 0f
        }
        return result
    }
}
