package com.uno24.wallpaper

import android.content.Context
import androidx.annotation.StringRes

enum class NumeralStyle(@StringRes val titleResId: Int) {
    ARABIC(R.string.numeral_style_arabic),
    ROMAN(R.string.numeral_style_roman),
    HEXADECIMAL(R.string.numeral_style_hex),
    BINARY(R.string.numeral_style_binary),
    OCTAL(R.string.numeral_style_octal),
    KANJI(R.string.numeral_style_kanji),
    DEVANAGARI(R.string.numeral_style_devanagari),
    EASTERN_ARABIC(R.string.numeral_style_eastern_arabic),
    GREEK(R.string.numeral_style_greek),
    HEBREW(R.string.numeral_style_hebrew);

    fun getTitle(context: Context): String = context.getString(titleResId)

    fun formatHour(hour: Int): String {
        val h = hour.coerceIn(0, 23)
        return when (this) {
            ARABIC -> String.format("%02d", h)
            ROMAN -> toRoman(h)
            HEXADECIMAL -> String.format("%02X", h)
            BINARY -> Integer.toBinaryString(h).padStart(5, '0')
            OCTAL -> String.format("%02o", h)
            KANJI -> toKanji(h)
            DEVANAGARI -> toDevanagari(h)
            EASTERN_ARABIC -> toEasternArabic(h)
            GREEK -> toGreek(h)
            HEBREW -> toHebrew(h)
        }
    }

    companion object {
        fun fromName(name: String?): NumeralStyle {
            return entries.firstOrNull { it.name == name } ?: ARABIC
        }

        fun toRoman(hour: Int): String {
            return when (hour) {
                0 -> "XXIV"
                1 -> "I"
                2 -> "II"
                3 -> "III"
                4 -> "IV"
                5 -> "V"
                6 -> "VI"
                7 -> "VII"
                8 -> "VIII"
                9 -> "IX"
                10 -> "X"
                11 -> "XI"
                12 -> "XII"
                13 -> "XIII"
                14 -> "XIV"
                15 -> "XV"
                16 -> "XVI"
                17 -> "XVII"
                18 -> "XVIII"
                19 -> "XIX"
                20 -> "XX"
                21 -> "XXI"
                22 -> "XXII"
                23 -> "XXIII"
                24 -> "XXIV"
                else -> String.format("%02d", hour)
            }
        }

        fun toKanji(hour: Int): String {
            if (hour == 0) return "〇"
            val digits = arrayOf("〇", "一", "二", "三", "四", "五", "六", "七", "八", "九")
            return when {
                hour < 10 -> digits[hour]
                hour == 10 -> "十"
                hour < 20 -> "十" + digits[hour % 10]
                hour == 20 -> "二十"
                else -> "二十" + digits[hour % 10]
            }
        }

        fun toDevanagari(hour: Int): String {
            val devanagariDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
            val s = String.format("%02d", hour)
            return s.map { devanagariDigits[it - '0'] }.joinToString("")
        }

        fun toEasternArabic(hour: Int): String {
            val arabicIndicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            val s = String.format("%02d", hour)
            return s.map { arabicIndicDigits[it - '0'] }.joinToString("")
        }

        fun toGreek(hour: Int): String {
            val h = if (hour == 0) 24 else hour
            val greekOnes = arrayOf("", "Α", "Β", "Γ", "Δ", "Ε", "Ϛ", "Ζ", "Η", "Θ")
            val greekTens = arrayOf("", "Ι", "Κ")
            val tens = h / 10
            val ones = h % 10
            return "${greekTens[tens]}${greekOnes[ones]}ʹ"
        }

        fun toHebrew(hour: Int): String {
            val h = if (hour == 0) 24 else hour
            return when (h) {
                1 -> "א׳"
                2 -> "ב׳"
                3 -> "ג׳"
                4 -> "ד׳"
                5 -> "ה׳"
                6 -> "ו׳"
                7 -> "ז׳"
                8 -> "ח׳"
                9 -> "ט׳"
                10 -> "י׳"
                11 -> "י״א"
                12 -> "י״ב"
                13 -> "י״ג"
                14 -> "י״ד"
                15 -> "ט״ו"
                16 -> "ט״ז"
                17 -> "י״ז"
                18 -> "י״ח"
                19 -> "י״ט"
                20 -> "כ׳"
                21 -> "כ״א"
                22 -> "כ״ב"
                23 -> "כ״ג"
                24 -> "כ״ד"
                else -> "כ״ד"
            }
        }
    }
}
