package com.watch1.app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager

data class ClockConfig(
    val lat: Double,
    val lon: Double,
    val theme: DialTheme,
    val showUv: Boolean,
    val showDate: Boolean = true,
    val showUvIndex: Boolean = true,
    val numeralStyle: NumeralStyle,
    val numeralOrientation: NumeralOrientation,
    val numeralDisplayMode: NumeralDisplayMode,
    val fontSizeScale: Float,
    val numeralFont: NumeralFont,
    val handStyle: HandStyle,
    val bgMode: BackgroundMode,
    val customColor: Int,
    val bezelStyle: BezelStyle = BezelStyle.NONE,
    val showBrandLogo: Boolean = true,
    val gradientDayNight: Boolean = false,
    val showMoonPhase: Boolean = true,
    val showGoldenHour: Boolean = false,
    val showSolarNoon: Boolean = false,
    val redNightMode: Boolean = false
)

object LocationHelper {
    const val PREFS_NAME = "WatchPrefs"
    private const val KEY_LAT = "key_lat"
    private const val KEY_LON = "key_lon"
    private const val KEY_THEME = "key_theme"
    private const val KEY_SHOW_UV = "key_show_uv"
    private const val KEY_SHOW_DATE = "key_show_date"
    private const val KEY_SHOW_UV_INDEX = "key_show_uv_index"
    private const val KEY_NUMERAL_STYLE = "key_numeral_style"
    private const val KEY_NUMERAL_ORIENTATION = "key_numeral_orientation"
    private const val KEY_NUMERAL_DISPLAY_MODE = "key_numeral_display_mode"
    private const val KEY_NUMERAL_SIZE = "key_numeral_size"
    private const val KEY_NUMERAL_FONT = "key_numeral_font"
    private const val KEY_HAND_STYLE = "key_hand_style"
    private const val KEY_BG_MODE = "key_bg_mode"
    private const val KEY_CUSTOM_COLOR = "key_custom_color"
    private const val KEY_FONT_SIZE_SCALE = "key_font_size_scale"
    private const val KEY_APP_LANGUAGE = "key_app_language"
    private const val KEY_BEZEL_STYLE = "key_bezel_style"
    private const val KEY_SHOW_BRAND_LOGO = "key_show_brand_logo"
    private const val KEY_GRADIENT_DAY_NIGHT = "key_gradient_day_night"
    private const val KEY_SHOW_MOON_PHASE = "key_show_moon_phase"
    private const val KEY_SHOW_GOLDEN_HOUR = "key_show_golden_hour"
    private const val KEY_SHOW_SOLAR_NOON = "key_show_solar_noon"
    private const val KEY_RED_NIGHT_MODE = "key_red_night_mode"

    private const val DEFAULT_LAT = 55.7558 // Moscow default
    private const val DEFAULT_LON = 37.6173

    fun getPrefs(context: Context): android.content.SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun loadConfig(context: Context): ClockConfig {
        val (lat, lon) = getSavedCoordinates(context)
        return ClockConfig(
            lat = lat,
            lon = lon,
            theme = getSavedTheme(context),
            showUv = getShowUv(context),
            showDate = getShowDate(context),
            showUvIndex = getShowUvIndex(context),
            numeralStyle = getNumeralStyle(context),
            numeralOrientation = getNumeralOrientation(context),
            numeralDisplayMode = getNumeralDisplayMode(context),
            fontSizeScale = getFontSizeScale(context),
            numeralFont = getNumeralFont(context),
            handStyle = getHandStyle(context),
            bgMode = getBackgroundMode(context),
            customColor = getCustomColor(context),
            bezelStyle = getBezelStyle(context),
            showBrandLogo = getShowBrandLogo(context),
            gradientDayNight = getGradientDayNight(context),
            showMoonPhase = getShowMoonPhase(context),
            showGoldenHour = getShowGoldenHour(context),
            showSolarNoon = getShowSolarNoon(context),
            redNightMode = getRedNightMode(context)
        )
    }

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

    fun getShowDate(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_DATE, true)
    }

    fun saveShowDate(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_DATE, enabled).apply()
    }

    fun getShowUvIndex(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_UV_INDEX, true)
    }

    fun saveShowUvIndex(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_UV_INDEX, enabled).apply()
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
        val name = prefs.getString(KEY_NUMERAL_DISPLAY_MODE, NumeralDisplayMode.ALL.name)
        return NumeralDisplayMode.fromName(name)
    }

    fun saveNumeralDisplayMode(context: Context, mode: NumeralDisplayMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NUMERAL_DISPLAY_MODE, mode.name).apply()
    }

    fun getFontSizeScale(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_FONT_SIZE_SCALE, 1.0f)
    }

    fun saveFontSizeScale(context: Context, scale: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val clamped = scale.coerceIn(0.5f, 1.8f)
        prefs.edit().putFloat(KEY_FONT_SIZE_SCALE, clamped).apply()
    }

    fun getNumeralFont(context: Context): NumeralFont {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_NUMERAL_FONT, NumeralFont.SANS_SERIF.name)
        return NumeralFont.fromName(name)
    }

    fun saveNumeralFont(context: Context, font: NumeralFont) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NUMERAL_FONT, font.name).apply()
    }

    fun getHandStyle(context: Context): HandStyle {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_HAND_STYLE, HandStyle.BOTTA_NEEDLE.name)
        return HandStyle.fromName(name)
    }

    fun saveHandStyle(context: Context, style: HandStyle) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_HAND_STYLE, style.name).apply()
    }

    fun getBackgroundMode(context: Context): BackgroundMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_BG_MODE, BackgroundMode.THEME_DEFAULT.name)
        return BackgroundMode.fromName(name)
    }

    fun saveBackgroundMode(context: Context, mode: BackgroundMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BG_MODE, mode.name).apply()
    }

    fun getCustomColor(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_CUSTOM_COLOR, Color.parseColor("#0A0F1D"))
    }

    fun saveCustomColor(context: Context, color: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_CUSTOM_COLOR, color).apply()
    }

    fun getAppLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_APP_LANGUAGE, AppLanguage.SYSTEM.code)
        return AppLanguage.fromCode(code)
    }

    fun saveAppLanguage(context: Context, language: AppLanguage) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_APP_LANGUAGE, language.code).apply()
        applyAppLanguage(language)
    }

    fun getBezelStyle(context: Context): BezelStyle {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_BEZEL_STYLE, BezelStyle.NONE.name)
        return BezelStyle.fromName(name)
    }

    fun saveBezelStyle(context: Context, style: BezelStyle) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BEZEL_STYLE, style.name).apply()
    }

    fun getShowBrandLogo(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_BRAND_LOGO, true)
    }

    fun saveShowBrandLogo(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_BRAND_LOGO, enabled).apply()
    }

    fun getGradientDayNight(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_GRADIENT_DAY_NIGHT, false)
    }

    fun saveGradientDayNight(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_GRADIENT_DAY_NIGHT, enabled).apply()
    }

    fun getShowMoonPhase(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_MOON_PHASE, true)
    }

    fun saveShowMoonPhase(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_MOON_PHASE, enabled).apply()
    }

    fun getShowGoldenHour(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_GOLDEN_HOUR, false)
    }

    fun saveShowGoldenHour(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_GOLDEN_HOUR, enabled).apply()
    }

    fun getShowSolarNoon(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_SOLAR_NOON, false)
    }

    fun saveShowSolarNoon(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_SOLAR_NOON, enabled).apply()
    }

    fun getRedNightMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_RED_NIGHT_MODE, false)
    }

    fun saveRedNightMode(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_RED_NIGHT_MODE, enabled).apply()
    }

    fun applyAppLanguage(language: AppLanguage) {
        val appLocales = if (language == AppLanguage.SYSTEM || language.code.isEmpty()) {
            androidx.core.os.LocaleListCompat.getEmptyLocaleList()
        } else {
            androidx.core.os.LocaleListCompat.forLanguageTags(language.code)
        }
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocales)
    }

    @SuppressLint("MissingPermission")
    fun updateLocationIfPermitted(context: Context, onLocationUpdated: (() -> Unit)? = null) {
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
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
                onLocationUpdated?.invoke()
            }

            // Also actively request fresh location updates from available providers
            for (provider in listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)) {
                if (lm.isProviderEnabled(provider)) {
                    val listener = object : android.location.LocationListener {
                        override fun onLocationChanged(loc: Location) {
                            saveCoordinates(context, loc.latitude, loc.longitude)
                            onLocationUpdated?.invoke()
                            try {
                                lm.removeUpdates(this)
                            } catch (e: Exception) {
                                // Ignore
                            }
                        }
                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }
                    lm.requestLocationUpdates(provider, 0L, 0f, listener, android.os.Looper.getMainLooper())
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted, fallback to saved/default
        }
    }
}
