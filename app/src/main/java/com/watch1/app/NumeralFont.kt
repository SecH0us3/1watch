package com.watch1.app

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.StringRes

enum class NumeralFont(@StringRes val titleResId: Int) {
    SANS_SERIF(R.string.font_sans_serif),
    SERIF(R.string.font_serif),
    MONOSPACE(R.string.font_monospace),
    CURSIVE(R.string.font_cursive);

    val typeface: Typeface get() = when (this) {
        SANS_SERIF -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        SERIF -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
        MONOSPACE -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        CURSIVE -> Typeface.create("cursive", Typeface.BOLD)
    }

    fun getTitle(context: Context): String = context.getString(titleResId)

    companion object {
        fun fromName(name: String?): NumeralFont {
            return values().firstOrNull { it.name == name } ?: SANS_SERIF
        }
    }
}
