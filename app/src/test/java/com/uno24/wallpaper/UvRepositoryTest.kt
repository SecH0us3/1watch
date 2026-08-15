package com.uno24.wallpaper

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class UvRepositoryTest {
    @Test
    fun testEstimateUvAtNoon() {
        val uvNoon = UvRepository.estimateUvFromSolarAngle(lat = 45.0, lon = 15.0, date = LocalDate.of(2026, 6, 21), hour = 12)
        assertTrue("Noon UV should be positive", uvNoon > 3.0f)
    }

    @Test
    fun testEstimateUvAtMidnight() {
        val uvMidnight = UvRepository.estimateUvFromSolarAngle(lat = 45.0, lon = 15.0, date = LocalDate.of(2026, 6, 21), hour = 0)
        assertTrue("Midnight UV should be 0", uvMidnight == 0.0f)
    }

    @Test
    fun testEstimateUvInPaphosCyprus() {
        val uvPaphos = UvRepository.estimateUvFromSolarAngle(lat = 34.77, lon = 32.42, date = LocalDate.of(2026, 8, 15), hour = 13)
        assertTrue("Midday UV in Paphos Cyprus in August should be >= 8.0", uvPaphos >= 8.0f)
    }
}
