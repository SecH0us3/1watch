package com.uno24.wallpaper

import android.graphics.Color

enum class DialTheme(
    val title: String,
    val dialBgColor: Int,
    val dayZoneColor: Int,
    val nightZoneColor: Int,
    val tickColor: Int,
    val textColor: Int,
    val handColor: Int,
    val pivotColor: Int
) {
    CLASSIC_DARK(
        title = "Classic Dark",
        dialBgColor = Color.parseColor("#121212"),
        dayZoneColor = Color.parseColor("#1E1E1E"),
        nightZoneColor = Color.parseColor("#08080C"),
        tickColor = Color.parseColor("#E0E0E0"),
        textColor = Color.parseColor("#CCCCCC"),
        handColor = Color.parseColor("#FF3D00"),
        pivotColor = Color.parseColor("#FF3D00")
    ),
    SOLAR_GOLD(
        title = "Solar Twilight",
        dialBgColor = Color.parseColor("#0B1021"),
        dayZoneColor = Color.parseColor("#1C2541"),
        nightZoneColor = Color.parseColor("#050814"),
        tickColor = Color.parseColor("#E0C068"),
        textColor = Color.parseColor("#D4AF37"),
        handColor = Color.parseColor("#FFB703"),
        pivotColor = Color.parseColor("#FFB703")
    ),
    MONOCHROME_LIGHT(
        title = "Minimal Light",
        dialBgColor = Color.parseColor("#E5E5EA"),
        dayZoneColor = Color.parseColor("#FFFFFF"),
        nightZoneColor = Color.parseColor("#1C1C1E"),
        tickColor = Color.parseColor("#1C1C1E"),
        textColor = Color.parseColor("#1C1C1E"),
        handColor = Color.parseColor("#000000"),
        pivotColor = Color.parseColor("#000000")
    ),
    CYBERPUNK(
        title = "Cyberpunk Neon",
        dialBgColor = Color.parseColor("#050510"),
        dayZoneColor = Color.parseColor("#0F172A"),
        nightZoneColor = Color.parseColor("#1E0038"),
        tickColor = Color.parseColor("#00F0FF"),
        textColor = Color.parseColor("#00F0FF"),
        handColor = Color.parseColor("#FF007A"),
        pivotColor = Color.parseColor("#FF007A")
    );

    companion object {
        fun fromName(name: String?): DialTheme {
            return values().firstOrNull { it.name == name } ?: CLASSIC_DARK
        }
    }
}
