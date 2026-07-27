package com.botta.uno24

import java.time.LocalDate
import kotlin.math.*

data class SunTimes(val sunriseHour: Double, val sunsetHour: Double)

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
        // 90.833° zenith for official sunrise/sunset (90° + 50' refraction)
        val zenith = Math.toRadians(90.833)
        
        val cosHourAngle = (cos(zenith) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
        
        // Clamp to [-1, 1] for extreme latitudes (midnight sun / polar night)
        val clampedCosHA = cosHourAngle.coerceIn(-1.0, 1.0)
        val hourAngle = Math.toDegrees(acos(clampedCosHA))

        val sunriseMinutes = 720.0 - 4.0 * (longitude + hourAngle) - eqTime + (timeZoneOffsetHours * 60.0)
        val sunsetMinutes = 720.0 - 4.0 * (longitude - hourAngle) - eqTime + (timeZoneOffsetHours * 60.0)

        val sunriseHour = (sunriseMinutes / 60.0).mod(24.0)
        val sunsetHour = (sunsetMinutes / 60.0).mod(24.0)

        return SunTimes(sunriseHour, sunsetHour)
    }
}
