package com.botta.uno24

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SolarCalculatorTest {

    @Test
    fun testEquinoxSolarTimesAtEquator() {
        // Equinox at equator: Sunrise approx 6.0, Sunset approx 18.0
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 0.0,
            longitude = 0.0,
            date = LocalDate.of(2026, 3, 20),
            timeZoneOffsetHours = 0.0
        )
        assertEquals(6.0, sunTimes.sunriseHour, 0.5)
        assertEquals(18.0, sunTimes.sunsetHour, 0.5)
    }

    @Test
    fun testSummerSolsticeNorthernHemisphere() {
        // Summer solstice at 50°N (e.g. Frankfurt/London approx latitude): longer day
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 50.0,
            longitude = 0.0,
            date = LocalDate.of(2026, 6, 21),
            timeZoneOffsetHours = 0.0
        )
        // Sunrise should be early (around 4.0 - 5.0) and sunset late (around 19.5 - 20.5)
        assertTrue("Sunrise should be before 5.0 AM, got ${sunTimes.sunriseHour}", sunTimes.sunriseHour < 5.0)
        assertTrue("Sunset should be after 19.5 PM, got ${sunTimes.sunsetHour}", sunTimes.sunsetHour > 19.5)
    }

    @Test
    fun testWinterSolsticeNorthernHemisphere() {
        // Winter solstice at 50°N: shorter day
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 50.0,
            longitude = 0.0,
            date = LocalDate.of(2026, 12, 21),
            timeZoneOffsetHours = 0.0
        )
        // Sunrise should be late (around 7.5 - 8.5) and sunset early (around 15.5 - 16.5)
        assertTrue("Sunrise should be after 7.5 AM, got ${sunTimes.sunriseHour}", sunTimes.sunriseHour > 7.5)
        assertTrue("Sunset should be before 16.5 PM, got ${sunTimes.sunsetHour}", sunTimes.sunsetHour < 16.5)
    }

    @Test
    fun testNewYorkEquinoxLocalTime() {
        // New York (40.7128° N, -74.0060° W), UTC-5 offset
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 40.7128,
            longitude = -74.0060,
            date = LocalDate.of(2026, 3, 20),
            timeZoneOffsetHours = -5.0
        )
        assertEquals(6.0, sunTimes.sunriseHour, 0.5)
        assertEquals(18.0, sunTimes.sunsetHour, 0.5)
    }
}
