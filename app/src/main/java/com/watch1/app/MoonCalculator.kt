package com.watch1.app

import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.PI

enum class MoonPhaseName {
    NEW_MOON,
    WAXING_CRESCENT,
    FIRST_QUARTER,
    WAXING_GIBBOUS,
    FULL_MOON,
    WANING_GIBBOUS,
    LAST_QUARTER,
    WANING_CRESCENT
}

data class MoonInfo(
    val phase: Double, // 0.0..1.0
    val illumination: Double, // 0.0..1.0 (0% to 100%)
    val phaseName: MoonPhaseName
)

object MoonCalculator {
    private const val SYNODIC_MONTH = 29.530588853
    // Reference New Moon epoch: 2024-01-11 11:57 UTC = 1704974220 epoch seconds
    private const val REFERENCE_NEW_MOON_EPOCH_SEC = 1704974220L

    fun calculateMoonInfo(date: LocalDate): MoonInfo {
        val epochSeconds = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond() + 43200 // Noon UTC
        val diffDays = (epochSeconds - REFERENCE_NEW_MOON_EPOCH_SEC).toDouble() / 86400.0
        val cycles = diffDays / SYNODIC_MONTH
        val phase = (cycles - floor(cycles)).mod(1.0)

        // Illumination fraction k = (1 - cos(2*pi*phase)) / 2
        val illumination = (1.0 - cos(2.0 * PI * phase)) / 2.0

        val name = when {
            phase < 0.03 || phase >= 0.97 -> MoonPhaseName.NEW_MOON
            phase < 0.22 -> MoonPhaseName.WAXING_CRESCENT
            phase < 0.28 -> MoonPhaseName.FIRST_QUARTER
            phase < 0.47 -> MoonPhaseName.WAXING_GIBBOUS
            phase < 0.53 -> MoonPhaseName.FULL_MOON
            phase < 0.72 -> MoonPhaseName.WANING_GIBBOUS
            phase < 0.78 -> MoonPhaseName.LAST_QUARTER
            else -> MoonPhaseName.WANING_CRESCENT
        }

        return MoonInfo(phase, illumination, name)
    }
}
