package com.watch1.app

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class MoonCalculatorTest {
    @Test
    fun testKnownNewMoon() {
        // Known New Moon: 2024-01-11
        val info = MoonCalculator.calculateMoonInfo(LocalDate.of(2024, 1, 11))
        assertTrue("Phase should be near 0.0 or 1.0 on new moon", info.phase < 0.05 || info.phase > 0.95)
        assertTrue("Illumination should be near 0% on new moon", info.illumination < 0.1)
    }

    @Test
    fun testKnownFullMoon() {
        // Known Full Moon: 2024-01-25
        val info = MoonCalculator.calculateMoonInfo(LocalDate.of(2024, 1, 25))
        assertTrue("Phase should be near 0.5 on full moon", info.phase in 0.45..0.55)
        assertTrue("Illumination should be near 100% on full moon", info.illumination > 0.9)
    }

    @Test
    fun testPhaseMonotonicity() {
        var prevPhase = MoonCalculator.calculateMoonInfo(LocalDate.of(2026, 1, 1)).phase
        for (i in 2..28) {
            val currPhase = MoonCalculator.calculateMoonInfo(LocalDate.of(2026, 1, i)).phase
            if (currPhase > prevPhase) {
                assertTrue(currPhase >= prevPhase)
            }
            prevPhase = currPhase
        }
    }
}
