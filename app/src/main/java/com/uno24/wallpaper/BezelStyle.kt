package com.uno24.wallpaper

import android.content.Context
import androidx.annotation.StringRes

enum class BezelStyle(
    @StringRes val titleResId: Int,
    val title: String
) {
    NONE(R.string.bezel_none, "None"),
    TITANIUM_BRUSHED(R.string.bezel_titanium_brushed, "Satin Titanium"),
    BLACK_CERAMIC(R.string.bezel_black_ceramic, "Black Ceramic"),
    POLISHED_GOLD(R.string.bezel_polished_gold, "Polished Gold");

    fun getTitle(context: Context): String = context.getString(titleResId)

    companion object {
        fun fromName(name: String?): BezelStyle {
            return entries.firstOrNull { it.name == name } ?: NONE
        }
    }
}
