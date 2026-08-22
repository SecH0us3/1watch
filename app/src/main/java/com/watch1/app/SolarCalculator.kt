package com.watch1.app

import java.time.LocalDate
import kotlin.math.*

data class SunTimes(
    val sunriseHour: Double,
    val sunsetHour: Double,
    val solarNoonHour: Double = 12.0,
    val morningBlueHourStart: Double? = null,
    val morningGoldenHourEnd: Double? = null,
    val eveningGoldenHourStart: Double? = null,
    val eveningBlueHourEnd: Double? = null,
    val isPolarDay: Boolean = false,
    val isPolarNight: Boolean = false
)

object SolarCalculator {
    fun calculateSunTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        timeZoneOffsetHours: Double
    ): SunTimes {
        val dayOfYear = date.dayOfYear
        val gamma = 2.0 * PI / 365.0 * (dayOfYear - 1 + (12.0 - 12.0) / 24.0)
        
        // Equation of time (minutes)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) - 0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))
        
        // Solar declination (radians)
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) - 0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma) - 0.002697 * cos(3 * gamma) + 0.00148 * sin(3 * gamma)

        val latRad = Math.toRadians(latitude)
        
        // Solar Noon in local time
        val solarNoonMinutes = 720.0 - 4.0 * longitude - eqTime + (timeZoneOffsetHours * 60.0)
        val solarNoonHour = (solarNoonMinutes / 60.0).mod(24.0)

        // Helper to calculate hour angle in degrees for a given zenith angle
        fun calculateHourAngle(zenithDeg: Double): Double? {
            val zenithRad = Math.toRadians(zenithDeg)
            val cosHA = (cos(zenithRad) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
            if (cosHA < -1.0 || cosHA > 1.0) return null
            return Math.toDegrees(acos(cosHA))
        }

        val officialHA = calculateHourAngle(90.833)
        if (officialHA == null) {
            val zenithRad = Math.toRadians(90.833)
            val cosHA = (cos(zenithRad) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
            return if (cosHA < -1.0) {
                SunTimes(sunriseHour = 0.0, sunsetHour = 24.0, solarNoonHour = solarNoonHour, isPolarDay = true, isPolarNight = false)
            } else {
                SunTimes(sunriseHour = 12.0, sunsetHour = 12.0, solarNoonHour = solarNoonHour, isPolarDay = false, isPolarNight = true)
            }
        }

        val sunriseMinutes = 720.0 - 4.0 * (longitude + officialHA) - eqTime + (timeZoneOffsetHours * 60.0)
        val sunsetMinutes = 720.0 - 4.0 * (longitude - officialHA) - eqTime + (timeZoneOffsetHours * 60.0)
        val sunriseHour = (sunriseMinutes / 60.0).mod(24.0)
        val sunsetHour = (sunsetMinutes / 60.0).mod(24.0)

        // Civil twilight (Blue Hour): zenith = 96.0 deg (sun at -6 deg)
        val civilHA = calculateHourAngle(96.0)
        val morningBlueHourStart = civilHA?.let { ha -> ((720.0 - 4.0 * (longitude + ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }
        val eveningBlueHourEnd = civilHA?.let { ha -> ((720.0 - 4.0 * (longitude - ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }

        // Golden hour: zenith = 84.0 deg (sun at +6 deg)
        val goldenHA = calculateHourAngle(84.0)
        val morningGoldenHourEnd = goldenHA?.let { ha -> ((720.0 - 4.0 * (longitude + ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }
        val eveningGoldenHourStart = goldenHA?.let { ha -> ((720.0 - 4.0 * (longitude - ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }

        return SunTimes(
            sunriseHour = sunriseHour,
            sunsetHour = sunsetHour,
            solarNoonHour = solarNoonHour,
            morningBlueHourStart = morningBlueHourStart,
            morningGoldenHourEnd = morningGoldenHourEnd,
            eveningGoldenHourStart = eveningGoldenHourStart,
            eveningBlueHourEnd = eveningBlueHourEnd,
            isPolarDay = false,
            isPolarNight = false
        )
    }
}
