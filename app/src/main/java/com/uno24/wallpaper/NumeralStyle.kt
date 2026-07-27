package com.uno24.wallpaper

enum class NumeralStyle {
    ARABIC,
    ROMAN;

    companion object {
        fun fromName(name: String?): NumeralStyle {
            return values().firstOrNull { it.name == name } ?: ARABIC
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
    }
}
