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
                // Network error, fallback used
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
