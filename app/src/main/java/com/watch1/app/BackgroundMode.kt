package com.watch1.app

import android.content.Context
import androidx.annotation.StringRes

enum class BackgroundMode(@StringRes val titleResId: Int) {
    THEME_DEFAULT(R.string.bg_mode_theme_default),
    CUSTOM_IMAGE(R.string.bg_mode_custom_image),
    CUSTOM_COLOR(R.string.bg_mode_custom_color);

    fun getTitle(context: Context): String = context.getString(titleResId)

    companion object {
        fun fromName(name: String?): BackgroundMode {
            return values().firstOrNull { it.name == name } ?: THEME_DEFAULT
        }
    }
}
