package com.uno24.wallpaper

enum class NumeralOrientation {
    UPRIGHT, // Facing user upright
    RADIAL;  // Rotated towards dial center

    companion object {
        fun fromName(name: String?): NumeralOrientation {
            return values().firstOrNull { it.name == name } ?: UPRIGHT
        }
    }
}
