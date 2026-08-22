package com.watch1.app

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class SolarCalculatorTest {
    @Test
    fun testSolarNoonCalculation() {
        // Moscow: 55.75 N, 37.61 E, UTC+3
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 55.75,
            longitude = 37.61,
            date = LocalDate.of(2026, 6, 21),
            timeZoneOffsetHours = 3.0
        )
        // Solar noon should be around 12:30 (12.5h) in Moscow
        assertEquals(12.5, sunTimes.solarNoonHour, 0.5)
        assertTrue(sunTimes.sunriseHour < sunTimes.solarNoonHour)
        assertTrue(sunTimes.sunsetHour > sunTimes.solarNoonHour)
    }

    @Test
    fun testGoldenAndBlueHours() {
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 55.75,
            longitude = 37.61,
            date = LocalDate.of(2026, 6, 21),
            timeZoneOffsetHours = 3.0
        )
        // Morning blue hour starts before sunrise
        assertNotNull(sunTimes.morningBlueHourStart)
        assertTrue(sunTimes.morningBlueHourStart!! < sunTimes.sunriseHour)

        // Morning golden hour starts at sunrise and ends after sunrise
        assertNotNull(sunTimes.morningGoldenHourEnd)
        assertTrue(sunTimes.morningGoldenHourEnd!! > sunTimes.sunriseHour)

        // Evening golden hour starts before sunset
        assertNotNull(sunTimes.eveningGoldenHourStart)
        assertTrue(sunTimes.eveningGoldenHourStart!! < sunTimes.sunsetHour)

        // Evening blue hour ends after sunset
        assertNotNull(sunTimes.eveningBlueHourEnd)
        assertTrue(sunTimes.eveningBlueHourEnd!! > sunTimes.sunsetHour)
    }
}
