package com.watch1.app

import android.content.Context
import androidx.annotation.StringRes

enum class NumeralDisplayMode(@StringRes val titleResId: Int) {
    EVEN_ONLY(R.string.display_mode_even),
    ODD_ONLY(R.string.display_mode_odd),
    ALL(R.string.display_mode_all);

    fun getTitle(context: Context): String = context.getString(titleResId)

    companion object {
        fun fromName(name: String?): NumeralDisplayMode {
            return values().firstOrNull { it.name == name } ?: ALL
        }
    }
}
