package com.botta.uno24

import org.junit.Assert.assertEquals
import org.junit.Test

class Uno24DialRendererTest {

    @Test
    fun testTimeToAngle_noon() {
        val angle = Uno24DialRenderer.timeToAngle(12.0)
        assertEquals(0f, angle, 0.001f)
    }

    @Test
    fun testTimeToAngle_1800() {
        val angle = Uno24DialRenderer.timeToAngle(18.0)
        assertEquals(90f, angle, 0.001f)
    }

    @Test
    fun testTimeToAngle_midnight() {
        val angle = Uno24DialRenderer.timeToAngle(0.0)
        assertEquals(180f, angle, 0.001f)
    }

    @Test
    fun testTimeToAngle_0600() {
        val angle = Uno24DialRenderer.timeToAngle(6.0)
        assertEquals(270f, angle, 0.001f)
    }

    @Test
    fun testTimeToAngle_fractions() {
        assertEquals(45f, Uno24DialRenderer.timeToAngle(15.0), 0.001f)
        assertEquals(135f, Uno24DialRenderer.timeToAngle(21.0), 0.001f)
        assertEquals(225f, Uno24DialRenderer.timeToAngle(3.0), 0.001f)
        assertEquals(315f, Uno24DialRenderer.timeToAngle(9.0), 0.001f)
    }
}
