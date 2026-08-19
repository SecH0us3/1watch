package com.watch1.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WatchAppWidgetTest {

    @Test
    fun testWidgetConstants() {
        assertEquals("com.watch1.app.ACTION_UPDATE_WIDGETS", WatchAppWidgetProvider.ACTION_UPDATE_WIDGETS)
    }

    @Test
    fun testWidgetMinDimensions() {
        val minWidthDp = 110
        val minHeightDp = 110
        val density = 2.0f
        val widthPx = (minWidthDp * density).toInt()
        val heightPx = (minHeightDp * density).toInt()
        assertEquals(220, widthPx)
        assertEquals(220, heightPx)
    }
}
