package com.watch1.app

import java.time.LocalDate
import kotlin.math.*

data class SunTimes(
    val sunriseHour: Double,
    val sunsetHour: Double,
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
        val zenith = Math.toRadians(90.833)
        
        val cosHourAngle = (cos(zenith) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
        
        if (cosHourAngle < -1.0) {
            // Sun never sets -> Polar Day
            return SunTimes(sunriseHour = 0.0, sunsetHour = 24.0, isPolarDay = true, isPolarNight = false)
        }
        if (cosHourAngle > 1.0) {
            // Sun never rises -> Polar Night
            return SunTimes(sunriseHour = 12.0, sunsetHour = 12.0, isPolarDay = false, isPolarNight = true)
        }

        val hourAngle = Math.toDegrees(acos(cosHourAngle))

        val sunriseMinutes = 720.0 - 4.0 * (longitude + hourAngle) - eqTime + (timeZoneOffsetHours * 60.0)
        val sunsetMinutes = 720.0 - 4.0 * (longitude - hourAngle) - eqTime + (timeZoneOffsetHours * 60.0)

        val sunriseHour = (sunriseMinutes / 60.0).mod(24.0)
        val sunsetHour = (sunsetMinutes / 60.0).mod(24.0)

        return SunTimes(sunriseHour, sunsetHour, isPolarDay = false, isPolarNight = false)
    }
}
