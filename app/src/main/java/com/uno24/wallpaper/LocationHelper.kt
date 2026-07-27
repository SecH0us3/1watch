package com.uno24.wallpaper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

object LocationHelper {
    private const val PREFS_NAME = "Uno24Prefs"
    private const val KEY_LAT = "key_lat"
    private const val KEY_LON = "key_lon"
    private const val KEY_THEME = "key_theme"
    private const val KEY_SHOW_UV = "key_show_uv"
    private const val KEY_NUMERAL_STYLE = "key_numeral_style"
    private const val KEY_NUMERAL_ORIENTATION = "key_numeral_orientation"
    private const val KEY_NUMERAL_DISPLAY_MODE = "key_numeral_display_mode"
    private const val KEY_NUMERAL_SIZE = "key_numeral_size"

    private const val DEFAULT_LAT = 55.7558 // Moscow default
    private const val DEFAULT_LON = 37.6173

    fun getSavedCoordinates(context: Context): Pair<Double, Double> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lat = prefs.getFloat(KEY_LAT, DEFAULT_LAT.toFloat()).toDouble()
        val lon = prefs.getFloat(KEY_LON, DEFAULT_LON.toFloat()).toDouble()
        return Pair(lat, lon)
    }

    fun saveCoordinates(context: Context, lat: Double, lon: Double) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat(KEY_LAT, lat.toFloat())
            .putFloat(KEY_LON, lon.toFloat())
            .apply()
    }

    fun getSavedTheme(context: Context): DialTheme {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val themeName = prefs.getString(KEY_THEME, DialTheme.CLASSIC_DARK.name)
        return DialTheme.fromName(themeName)
    }

    fun saveTheme(context: Context, theme: DialTheme) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun getShowUv(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_UV, true)
    }

    fun saveShowUv(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_UV, enabled).apply()
    }

    fun getNumeralStyle(context: Context): NumeralStyle {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_NUMERAL_STYLE, NumeralStyle.ARABIC.name)
        return NumeralStyle.fromName(name)
    }

    fun saveNumeralStyle(context: Context, style: NumeralStyle) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NUMERAL_STYLE, style.name).apply()
    }

    fun getNumeralOrientation(context: Context): NumeralOrientation {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_NUMERAL_ORIENTATION, NumeralOrientation.UPRIGHT.name)
        return NumeralOrientation.fromName(name)
    }

    fun saveNumeralOrientation(context: Context, orientation: NumeralOrientation) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NUMERAL_ORIENTATION, orientation.name).apply()
    }

    fun getNumeralDisplayMode(context: Context): NumeralDisplayMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_NUMERAL_DISPLAY_MODE, NumeralDisplayMode.EVEN_ONLY.name)
        return NumeralDisplayMode.fromName(name)
    }

    fun saveNumeralDisplayMode(context: Context, mode: NumeralDisplayMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NUMERAL_DISPLAY_MODE, mode.name).apply()
    }

    fun getNumeralSize(context: Context): NumeralSize {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_NUMERAL_SIZE, NumeralSize.MEDIUM.name)
        return NumeralSize.fromName(name)
    }

    fun saveNumeralSize(context: Context, size: NumeralSize) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NUMERAL_SIZE, size.name).apply()
    }

    @SuppressLint("MissingPermission")
    fun updateLocationIfPermitted(context: Context) {
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = lm.getProviders(true)
            var bestLocation: Location? = null

            for (provider in providers) {
                val l = lm.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }

            bestLocation?.let {
                saveCoordinates(context, it.latitude, it.longitude)
            }
        } catch (e: SecurityException) {
            // Permission not granted, fallback to saved/default
        }
    }
}
