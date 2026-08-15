package com.uno24.wallpaper

import android.content.Context
import androidx.annotation.StringRes

enum class HandStyle(@StringRes val titleResId: Int) {
    BOTTA_NEEDLE(R.string.hand_style_botta_needle),
    BAUHAUS_BATON(R.string.hand_style_bauhaus_baton),
    ARROW_SPORT(R.string.hand_style_arrow_sport),
    SWORD_AVIO(R.string.hand_style_sword_avio),
    SKELETON_RING(R.string.hand_style_skeleton_ring),
    SPIRAL_CURVE(R.string.hand_style_spiral_curve),
    SPIRAL_VORTEX(R.string.hand_style_spiral_vortex),
    SPIRAL_DOUBLE_DNA(R.string.hand_style_spiral_double_dna),
    SPIRAL_FLAME(R.string.hand_style_spiral_flame);

    fun getTitle(context: Context): String = context.getString(titleResId)

    companion object {
        fun fromName(name: String?): HandStyle {
            return values().firstOrNull { it.name == name } ?: BOTTA_NEEDLE
        }
    }
}
