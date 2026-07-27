package com.uno24.wallpaper

enum class NumeralSize(val title: String, val scale: Float) {
    SMALL("Мелкие", 0.75f),
    MEDIUM("Средние", 1.0f),
    LARGE("Крупные", 1.25f);

    companion object {
        fun fromName(name: String?): NumeralSize {
            return values().firstOrNull { it.name == name } ?: MEDIUM
        }
    }
}
