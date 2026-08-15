package com.uno24.wallpaper

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.StringRes

enum class NumeralFont(@StringRes val titleResId: Int, val typeface: Typeface) {
    SANS_SERIF(R.string.font_sans_serif, Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)),
    SERIF(R.string.font_serif, Typeface.create(Typeface.SERIF, Typeface.BOLD)),
    MONOSPACE(R.string.font_monospace, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)),
    CURSIVE(R.string.font_cursive, Typeface.create("cursive", Typeface.BOLD));

    fun getTitle(context: Context): String = context.getString(titleResId)

    companion object {
        fun fromName(name: String?): NumeralFont {
            return values().firstOrNull { it.name == name } ?: SANS_SERIF
        }
    }
}
