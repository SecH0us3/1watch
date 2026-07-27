package com.uno24.wallpaper

enum class BackgroundMode(val title: String) {
    THEME_DEFAULT("По теме"),
    CUSTOM_IMAGE("Фото из галереи"),
    CUSTOM_COLOR("Свой цвет");

    companion object {
        fun fromName(name: String?): BackgroundMode {
            return values().firstOrNull { it.name == name } ?: THEME_DEFAULT
        }
    }
}
