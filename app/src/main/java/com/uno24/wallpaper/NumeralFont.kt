package com.uno24.wallpaper

import android.graphics.Typeface

enum class NumeralFont(val title: String, val typeface: Typeface) {
    SANS_SERIF("Sans-Serif (Стандартный)", Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)),
    SERIF("Serif (С засечками)", Typeface.create(Typeface.SERIF, Typeface.BOLD)),
    MONOSPACE("Monospace (Моноширинный)", Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)),
    CURSIVE("Cursive (Курсив)", Typeface.create("cursive", Typeface.BOLD));

    companion object {
        fun fromName(name: String?): NumeralFont {
            return values().firstOrNull { it.name == name } ?: SANS_SERIF
        }
    }
}
