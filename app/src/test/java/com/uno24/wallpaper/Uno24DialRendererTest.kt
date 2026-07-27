package com.uno24.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Test

class Uno24DialRendererTest {
    @Test
    fun testTimeToAngle() {
        assertEquals(0f, Uno24DialRenderer.timeToAngle(12.0), 0.01f)
        assertEquals(90f, Uno24DialRenderer.timeToAngle(18.0), 0.01f)
        assertEquals(180f, Uno24DialRenderer.timeToAngle(0.0), 0.01f)
        assertEquals(270f, Uno24DialRenderer.timeToAngle(6.0), 0.01f)
    }
}
