package com.uno24.wallpaper

import android.content.Context
import androidx.annotation.StringRes

enum class DialTheme(
    @StringRes val titleResId: Int,
    val title: String,
    val dialBgColor: Int,
    val dayZoneColor: Int,
    val nightZoneColor: Int,
    val dayTickColor: Int,
    val nightTickColor: Int,
    val dayTextColor: Int,
    val nightTextColor: Int,
    val handColor: Int,
    val pivotColor: Int
) {
    CLASSIC_DARK(
        titleResId = R.string.theme_classic_dark,
        title = "Classic Dark",
        dialBgColor = 0xFF121212.toInt(),
        dayZoneColor = 0xFF222228.toInt(),
        nightZoneColor = 0xFF08080C.toInt(),
        dayTickColor = 0xFFCCCCCC.toInt(),
        nightTickColor = 0xFFE0E0E0.toInt(),
        dayTextColor = 0xFFBDBDBD.toInt(),
        nightTextColor = 0xFFE0E0E0.toInt(),
        handColor = 0xFFFF3D00.toInt(),
        pivotColor = 0xFFFF3D00.toInt()
    ),
    SOLAR_GOLD(
        titleResId = R.string.theme_solar_gold,
        title = "Solar Twilight",
        dialBgColor = 0xFF0B1021.toInt(),
        dayZoneColor = 0xFF1C2541.toInt(),
        nightZoneColor = 0xFF050814.toInt(),
        dayTickColor = 0xFFD4AF37.toInt(),
        nightTickColor = 0xFFFFE082.toInt(),
        dayTextColor = 0xFFD4AF37.toInt(),
        nightTextColor = 0xFFFFE082.toInt(),
        handColor = 0xFFFFB703.toInt(),
        pivotColor = 0xFFFFB703.toInt()
    ),
    MONOCHROME_LIGHT(
        titleResId = R.string.theme_monochrome_light,
        title = "Minimal Light",
        dialBgColor = 0xFFE5E5EA.toInt(),
        dayZoneColor = 0xFFFFFFFF.toInt(),
        nightZoneColor = 0xFF18181B.toInt(),
        dayTickColor = 0xFF18181B.toInt(),
        nightTickColor = 0xFFFFFFFF.toInt(),
        dayTextColor = 0xFF18181B.toInt(),
        nightTextColor = 0xFFFFFFFF.toInt(),
        handColor = 0xFFFF3D00.toInt(),
        pivotColor = 0xFFFF3D00.toInt()
    ),
    ARCTIC_WHITE(
        titleResId = R.string.theme_arctic_white,
        title = "Arctic Silver",
        dialBgColor = 0xFFE2E8F0.toInt(),
        dayZoneColor = 0xFFF8FAFC.toInt(),
        nightZoneColor = 0xFF0F172A.toInt(),
        dayTickColor = 0xFF1E293B.toInt(),
        nightTickColor = 0xFFF8FAFC.toInt(),
        dayTextColor = 0xFF0F172A.toInt(),
        nightTextColor = 0xFFF8FAFC.toInt(),
        handColor = 0xFF0284C7.toInt(),
        pivotColor = 0xFF0284C7.toInt()
    ),
    PORCELAIN_BLUE(
        titleResId = R.string.theme_porcelain_blue,
        title = "Porcelain Cobalt",
        dialBgColor = 0xFFDCE4EC.toInt(),
        dayZoneColor = 0xFFFFFFFF.toInt(),
        nightZoneColor = 0xFF0A192F.toInt(),
        dayTickColor = 0xFF0A192F.toInt(),
        nightTickColor = 0xFFE0E7FF.toInt(),
        dayTextColor = 0xFF0A192F.toInt(),
        nightTextColor = 0xFFE0E7FF.toInt(),
        handColor = 0xFF0047AB.toInt(),
        pivotColor = 0xFF0047AB.toInt()
    ),
    DESERT_SAND(
        titleResId = R.string.theme_desert_sand,
        title = "Desert Linen",
        dialBgColor = 0xFFE8E2D5.toInt(),
        dayZoneColor = 0xFFFAF6ED.toInt(),
        nightZoneColor = 0xFF261E1A.toInt(),
        dayTickColor = 0xFF2D231E.toInt(),
        nightTickColor = 0xFFFAF6ED.toInt(),
        dayTextColor = 0xFF2D231E.toInt(),
        nightTextColor = 0xFFFAF6ED.toInt(),
        handColor = 0xFFD9531E.toInt(),
        pivotColor = 0xFFD9531E.toInt()
    ),
    SAKURA_DAWN(
        titleResId = R.string.theme_sakura_dawn,
        title = "Sakura Dawn",
        dialBgColor = 0xFFF2E6E6.toInt(),
        dayZoneColor = 0xFFFFF7F7.toInt(),
        nightZoneColor = 0xFF2D1927.toInt(),
        dayTickColor = 0xFF3D2132.toInt(),
        nightTickColor = 0xFFFFF7F7.toInt(),
        dayTextColor = 0xFF3D2132.toInt(),
        nightTextColor = 0xFFFFF7F7.toInt(),
        handColor = 0xFFE11D48.toInt(),
        pivotColor = 0xFFE11D48.toInt()
    ),
    SAGE_MINT(
        titleResId = R.string.theme_sage_mint,
        title = "Nordic Sage",
        dialBgColor = 0xFFDDE5DE.toInt(),
        dayZoneColor = 0xFFF4FAF5.toInt(),
        nightZoneColor = 0xFF132219.toInt(),
        dayTickColor = 0xFF193124.toInt(),
        nightTickColor = 0xFFF4FAF5.toInt(),
        dayTextColor = 0xFF193124.toInt(),
        nightTextColor = 0xFFF4FAF5.toInt(),
        handColor = 0xFF15803D.toInt(),
        pivotColor = 0xFF15803D.toInt()
    ),
    CYBERPUNK(
        titleResId = R.string.theme_cyberpunk,
        title = "Cyberpunk Neon",
        dialBgColor = 0xFF050510.toInt(),
        dayZoneColor = 0xFF0F172A.toInt(),
        nightZoneColor = 0xFF1E0038.toInt(),
        dayTickColor = 0xFF00F0FF.toInt(),
        nightTickColor = 0xFFFF007A.toInt(),
        dayTextColor = 0xFF00F0FF.toInt(),
        nightTextColor = 0xFFFF007A.toInt(),
        handColor = 0xFFFF007A.toInt(),
        pivotColor = 0xFFFF007A.toInt()
    ),
    ROSE_GOLD_ONYX(
        titleResId = R.string.theme_rose_gold_onyx,
        title = "Rose Gold & Onyx",
        dialBgColor = 0xFF141212.toInt(),
        dayZoneColor = 0xFFEFEBE9.toInt(),
        nightZoneColor = 0xFF181514.toInt(),
        dayTickColor = 0xFF4E342E.toInt(),
        nightTickColor = 0xFFE0A899.toInt(),
        dayTextColor = 0xFF3E2723.toInt(),
        nightTextColor = 0xFFE0A899.toInt(),
        handColor = 0xFFE0A899.toInt(),
        pivotColor = 0xFFC58371.toInt()
    ),
    ROYAL_EMERALD(
        titleResId = R.string.theme_royal_emerald,
        title = "Royal Emerald & Gold",
        dialBgColor = 0xFF07140C.toInt(),
        dayZoneColor = 0xFFE8F5E9.toInt(),
        nightZoneColor = 0xFF0B1E13.toInt(),
        dayTickColor = 0xFF1B4332.toInt(),
        nightTickColor = 0xFFF3C644.toInt(),
        dayTextColor = 0xFF1B4332.toInt(),
        nightTextColor = 0xFFF3C644.toInt(),
        handColor = 0xFFF3C644.toInt(),
        pivotColor = 0xFFD4AF37.toInt()
    ),
    URUSHI_JAPAN(
        titleResId = R.string.theme_urushi_japan,
        title = "Japanese Urushi & Cinnabar",
        dialBgColor = 0xFF0D0D0E.toInt(),
        dayZoneColor = 0xFFF8F3E6.toInt(),
        nightZoneColor = 0xFF121214.toInt(),
        dayTickColor = 0xFF2B2D42.toInt(),
        nightTickColor = 0xFFE5B25D.toInt(),
        dayTextColor = 0xFF1D1E2C.toInt(),
        nightTextColor = 0xFFE5B25D.toInt(),
        handColor = 0xFFE63946.toInt(),
        pivotColor = 0xFFC1121F.toInt()
    ),
    ARABIAN_LAPIS(
        titleResId = R.string.theme_arabian_lapis,
        title = "Arabian Lapis Lazuli & Gold",
        dialBgColor = 0xFF060E18.toInt(),
        dayZoneColor = 0xFFEDF2F7.toInt(),
        nightZoneColor = 0xFF0B192C.toInt(),
        dayTickColor = 0xFF1E3E62.toInt(),
        nightTickColor = 0xFFFFD700.toInt(),
        dayTextColor = 0xFF1E3E62.toInt(),
        nightTextColor = 0xFFFFD700.toInt(),
        handColor = 0xFFFFD700.toInt(),
        pivotColor = 0xFFDAA520.toInt()
    ),
    NORDIC_PLATINUM(
        titleResId = R.string.theme_nordic_platinum,
        title = "Nordic Platinum & Titanium",
        dialBgColor = 0xFF141618.toInt(),
        dayZoneColor = 0xFFF1F3F5.toInt(),
        nightZoneColor = 0xFF1E2022.toInt(),
        dayTickColor = 0xFF334155.toInt(),
        nightTickColor = 0xFFE2E8F0.toInt(),
        dayTextColor = 0xFF334155.toInt(),
        nightTextColor = 0xFFE2E8F0.toInt(),
        handColor = 0xFFE2E8F0.toInt(),
        pivotColor = 0xFF94A3B8.toInt()
    );

    val tickColor: Int get() = dayTickColor
    val textColor: Int get() = dayTextColor

    fun getTitle(context: Context): String = context.getString(titleResId)

    fun next(): DialTheme {
        val vals = entries
        return vals[(ordinal + 1) % vals.size]
    }

    fun previous(): DialTheme {
        val vals = entries
        return vals[(ordinal - 1 + vals.size) % vals.size]
    }

    companion object {
        fun fromName(name: String?): DialTheme {
            return entries.firstOrNull { it.name == name } ?: CLASSIC_DARK
        }
    }
}
