package com.uno24.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class SolarCalculatorTest {
    @Test
    fun testEquinoxSolarTimesAtEquator() {
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 0.0,
            longitude = 0.0,
            date = LocalDate.of(2026, 3, 20),
            timeZoneOffsetHours = 0.0
        )
        assertEquals(6.0, sunTimes.sunriseHour, 0.5)
        assertEquals(18.0, sunTimes.sunsetHour, 0.5)
    }
}
