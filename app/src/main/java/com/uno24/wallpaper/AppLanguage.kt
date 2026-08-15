package com.uno24.wallpaper

enum class AppLanguage(val code: String, val displayName: String) {
    SYSTEM("", "System Default (По умолчанию)"),
    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский"),
    SPANISH("es", "Español"),
    GERMAN("de", "Deutsch"),
    FRENCH("fr", "Français"),
    CHINESE("zh-CN", "简体中文 (Chinese)"),
    JAPANESE("ja", "日本語 (Japanese)"),
    HINDI("hi", "हिन्दी (Hindi)"),
    LATIN("la", "Latina (Latin)"),
    GREEK("el", "Ἑλληνική (Greek)"),
    OLD_ENGLISH("ang", "Ænglisc (Old English)");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return values().firstOrNull { it.code == code } ?: SYSTEM
        }
    }
}
