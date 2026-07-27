package com.uno24.wallpaper

enum class NumeralDisplayMode(val title: String) {
    EVEN_ONLY("Только чётные (0, 2, 4...)"),
    ODD_ONLY("Только нечётные (1, 3, 5...)"),
    ALL("Все цифры (0..23)");

    companion object {
        fun fromName(name: String?): NumeralDisplayMode {
            return values().firstOrNull { it.name == name } ?: EVEN_ONLY
        }
    }
}
