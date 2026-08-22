package com.watch1.app

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WatchDialRendererTest {
    @Test
    fun testTimeToAngle() {
        assertEquals(0f, WatchDialRenderer.timeToAngle(12.0), 0.01f)
        assertEquals(90f, WatchDialRenderer.timeToAngle(18.0), 0.01f)
        assertEquals(180f, WatchDialRenderer.timeToAngle(0.0), 0.01f)
        assertEquals(270f, WatchDialRenderer.timeToAngle(6.0), 0.01f)
    }

    @Test
    fun testTimeToAngleQuarterHours() {
        // 12:15 = 12.25h -> 0.25 * 15 = 3.75 deg
        assertEquals(3.75f, WatchDialRenderer.timeToAngle(12.25), 0.01f)
        // 12:30 = 12.5h -> 0.5 * 15 = 7.5 deg
        assertEquals(7.5f, WatchDialRenderer.timeToAngle(12.5), 0.01f)
        // 12:45 = 12.75h -> 0.75 * 15 = 11.25 deg
        assertEquals(11.25f, WatchDialRenderer.timeToAngle(12.75), 0.01f)
    }

    @Test
    fun testHandStyleEnum() {
        assertEquals(HandStyle.BOTTA_NEEDLE, HandStyle.fromName("BOTTA_NEEDLE"))
        assertEquals(HandStyle.BAUHAUS_BATON, HandStyle.fromName("BAUHAUS_BATON"))
        assertEquals(HandStyle.ARROW_SPORT, HandStyle.fromName("ARROW_SPORT"))
        assertEquals(HandStyle.SWORD_AVIO, HandStyle.fromName("SWORD_AVIO"))
        assertEquals(HandStyle.SKELETON_RING, HandStyle.fromName("SKELETON_RING"))
        assertEquals(HandStyle.SPIRAL_CURVE, HandStyle.fromName("SPIRAL_CURVE"))
        assertEquals(HandStyle.SPIRAL_VORTEX, HandStyle.fromName("SPIRAL_VORTEX"))
        assertEquals(HandStyle.SPIRAL_DOUBLE_DNA, HandStyle.fromName("SPIRAL_DOUBLE_DNA"))
        assertEquals(HandStyle.SPIRAL_FLAME, HandStyle.fromName("SPIRAL_FLAME"))
        // fallback
        assertEquals(HandStyle.BOTTA_NEEDLE, HandStyle.fromName("UNKNOWN"))
    }

    @Test
    fun testAppLanguageEnum() {
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromCode(""))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.RUSSIAN, AppLanguage.fromCode("ru"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromCode("es"))
        assertEquals(AppLanguage.GERMAN, AppLanguage.fromCode("de"))
        assertEquals(AppLanguage.FRENCH, AppLanguage.fromCode("fr"))
        assertEquals(AppLanguage.CHINESE, AppLanguage.fromCode("zh-CN"))
        assertEquals(AppLanguage.JAPANESE, AppLanguage.fromCode("ja"))
        assertEquals(AppLanguage.HINDI, AppLanguage.fromCode("hi"))
        assertEquals(AppLanguage.LATIN, AppLanguage.fromCode("la"))
        assertEquals(AppLanguage.GREEK, AppLanguage.fromCode("el"))
        assertEquals(AppLanguage.OLD_ENGLISH, AppLanguage.fromCode("ang"))
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromCode("unknown_code"))
    }

    @Test
    fun testDialThemeEnum() {
        assertEquals(DialTheme.CLASSIC_DARK, DialTheme.fromName("CLASSIC_DARK"))
        assertEquals(DialTheme.SOLAR_GOLD, DialTheme.fromName("SOLAR_GOLD"))
        assertEquals(DialTheme.MONOCHROME_LIGHT, DialTheme.fromName("MONOCHROME_LIGHT"))
        assertEquals(DialTheme.ARCTIC_WHITE, DialTheme.fromName("ARCTIC_WHITE"))
        assertEquals(DialTheme.PORCELAIN_BLUE, DialTheme.fromName("PORCELAIN_BLUE"))
        assertEquals(DialTheme.DESERT_SAND, DialTheme.fromName("DESERT_SAND"))
        assertEquals(DialTheme.SAKURA_DAWN, DialTheme.fromName("SAKURA_DAWN"))
        assertEquals(DialTheme.SAGE_MINT, DialTheme.fromName("SAGE_MINT"))
        assertEquals(DialTheme.CYBERPUNK, DialTheme.fromName("CYBERPUNK"))
        assertEquals(DialTheme.ROSE_GOLD_ONYX, DialTheme.fromName("ROSE_GOLD_ONYX"))
        assertEquals(DialTheme.ROYAL_EMERALD, DialTheme.fromName("ROYAL_EMERALD"))
        assertEquals(DialTheme.URUSHI_JAPAN, DialTheme.fromName("URUSHI_JAPAN"))
        assertEquals(DialTheme.ARABIAN_LAPIS, DialTheme.fromName("ARABIAN_LAPIS"))
        assertEquals(DialTheme.NORDIC_PLATINUM, DialTheme.fromName("NORDIC_PLATINUM"))
        // fallback
        assertEquals(DialTheme.CLASSIC_DARK, DialTheme.fromName("UNKNOWN"))
    }

    @Test
    fun testNumeralDisplayModeEnum() {
        assertEquals(NumeralDisplayMode.ALL, NumeralDisplayMode.fromName("ALL"))
        assertEquals(NumeralDisplayMode.EVEN_ONLY, NumeralDisplayMode.fromName("EVEN_ONLY"))
        assertEquals(NumeralDisplayMode.ODD_ONLY, NumeralDisplayMode.fromName("ODD_ONLY"))
        // fallback
        assertEquals(NumeralDisplayMode.ALL, NumeralDisplayMode.fromName("UNKNOWN"))
    }

    @Test
    fun testNumeralStyleEnumAndFormatting() {
        assertEquals(NumeralStyle.ARABIC, NumeralStyle.fromName("ARABIC"))
        assertEquals(NumeralStyle.ROMAN, NumeralStyle.fromName("ROMAN"))
        assertEquals(NumeralStyle.HEXADECIMAL, NumeralStyle.fromName("HEXADECIMAL"))
        assertEquals(NumeralStyle.BINARY, NumeralStyle.fromName("BINARY"))
        assertEquals(NumeralStyle.OCTAL, NumeralStyle.fromName("OCTAL"))
        // fallback
        assertEquals(NumeralStyle.ARABIC, NumeralStyle.fromName("UNKNOWN"))

        assertEquals("00", NumeralStyle.ARABIC.formatHour(0))
        assertEquals("12", NumeralStyle.ARABIC.formatHour(12))
        assertEquals("XXIV", NumeralStyle.ROMAN.formatHour(0))
        assertEquals("XII", NumeralStyle.ROMAN.formatHour(12))
        assertEquals("00", NumeralStyle.HEXADECIMAL.formatHour(0))
        assertEquals("0C", NumeralStyle.HEXADECIMAL.formatHour(12))
        assertEquals("17", NumeralStyle.HEXADECIMAL.formatHour(23))
        assertEquals("00000", NumeralStyle.BINARY.formatHour(0))
        assertEquals("01100", NumeralStyle.BINARY.formatHour(12))
        assertEquals("10111", NumeralStyle.BINARY.formatHour(23))
        assertEquals("00", NumeralStyle.OCTAL.formatHour(0))
        assertEquals("14", NumeralStyle.OCTAL.formatHour(12))
        assertEquals("27", NumeralStyle.OCTAL.formatHour(23))
    }

    @Test
    fun testEthnicNumeralStyles() {
        assertEquals(NumeralStyle.KANJI, NumeralStyle.fromName("KANJI"))
        assertEquals(NumeralStyle.DEVANAGARI, NumeralStyle.fromName("DEVANAGARI"))
        assertEquals(NumeralStyle.EASTERN_ARABIC, NumeralStyle.fromName("EASTERN_ARABIC"))
        assertEquals(NumeralStyle.GREEK, NumeralStyle.fromName("GREEK"))
        assertEquals(NumeralStyle.HEBREW, NumeralStyle.fromName("HEBREW"))

        assertEquals("〇", NumeralStyle.KANJI.formatHour(0))
        assertEquals("十二", NumeralStyle.KANJI.formatHour(12))
        assertEquals("二十三", NumeralStyle.KANJI.formatHour(23))

        assertEquals("००", NumeralStyle.DEVANAGARI.formatHour(0))
        assertEquals("१२", NumeralStyle.DEVANAGARI.formatHour(12))
        assertEquals("२३", NumeralStyle.DEVANAGARI.formatHour(23))

        assertEquals("٠٠", NumeralStyle.EASTERN_ARABIC.formatHour(0))
        assertEquals("١٢", NumeralStyle.EASTERN_ARABIC.formatHour(12))
        assertEquals("٢٣", NumeralStyle.EASTERN_ARABIC.formatHour(23))

        assertEquals("ΚΔʹ", NumeralStyle.GREEK.formatHour(0))
        assertEquals("ΙΒʹ", NumeralStyle.GREEK.formatHour(12))
        assertEquals("ΚΓʹ", NumeralStyle.GREEK.formatHour(23))

        assertEquals("כ״ד", NumeralStyle.HEBREW.formatHour(0))
        assertEquals("י״ב", NumeralStyle.HEBREW.formatHour(12))
        assertEquals("כ״ג", NumeralStyle.HEBREW.formatHour(23))
    }

    @Test
    fun test12TierUvColorScale() {
        assertEquals(0, WatchDialRenderer.getUvColor(0.0f))
        assertEquals(0, WatchDialRenderer.getUvColor(0.4f))
        assertEquals(0xFF4CAF50.toInt(), WatchDialRenderer.getUvColor(1.0f))  // 0.5..1.4 (Emerald Green)
        assertEquals(0xFF8BC34A.toInt(), WatchDialRenderer.getUvColor(2.0f))  // 1.5..2.4 (Lime)
        assertEquals(0xFFCDDC39.toInt(), WatchDialRenderer.getUvColor(3.0f))  // 2.5..3.4 (Chartreuse)
        assertEquals(0xFFFFEB3B.toInt(), WatchDialRenderer.getUvColor(4.0f))  // 3.5..4.4 (Lemon Yellow)
        assertEquals(0xFFFDD835.toInt(), WatchDialRenderer.getUvColor(5.0f))  // 4.5..5.4 (Amber Gold)
        assertEquals(0xFFFFB300.toInt(), WatchDialRenderer.getUvColor(6.0f))  // 5.5..6.4 (Honey Amber)
        assertEquals(0xFFFB8C00.toInt(), WatchDialRenderer.getUvColor(7.0f))  // 6.5..7.4 (Tangerine Orange)
        assertEquals(0xFFFF5722.toInt(), WatchDialRenderer.getUvColor(8.0f))  // 7.5..8.4 (Flame Coral - Paphos UV 8)
        assertEquals(0xFFE53935.toInt(), WatchDialRenderer.getUvColor(9.0f))  // 8.5..9.4 (Crimson Red)
        assertEquals(0xFFD81B60.toInt(), WatchDialRenderer.getUvColor(10.0f)) // 9.5..10.4 (Ruby Magenta)
        assertEquals(0xFF9C27B0.toInt(), WatchDialRenderer.getUvColor(11.0f)) // 10.5..11.4 (Amethyst Purple)
        assertEquals(0xFF6A1B9A.toInt(), WatchDialRenderer.getUvColor(12.0f)) // 11.5+ (Deep UV Violet)
    }

    @Test
    fun testBezelStyleEnum() {
        assertEquals(BezelStyle.NONE, BezelStyle.fromName("NONE"))
        assertEquals(BezelStyle.TITANIUM_BRUSHED, BezelStyle.fromName("TITANIUM_BRUSHED"))
        assertEquals(BezelStyle.BLACK_CERAMIC, BezelStyle.fromName("BLACK_CERAMIC"))
        assertEquals(BezelStyle.POLISHED_GOLD, BezelStyle.fromName("POLISHED_GOLD"))
        // fallback
        assertEquals(BezelStyle.NONE, BezelStyle.fromName("UNKNOWN"))
    }

    @Test
    fun testClockConfigDefaultGradientDayNight() {
        val config = ClockConfig(
            lat = 55.75,
            lon = 37.61,
            theme = DialTheme.CLASSIC_DARK,
            showUv = true,
            numeralStyle = NumeralStyle.ARABIC,
            numeralOrientation = NumeralOrientation.UPRIGHT,
            numeralDisplayMode = NumeralDisplayMode.ALL,
            fontSizeScale = 1.0f,
            numeralFont = NumeralFont.SANS_SERIF,
            handStyle = HandStyle.BOTTA_NEEDLE,
            bgMode = BackgroundMode.THEME_DEFAULT,
            customColor = 0
        )
        assertEquals(false, config.gradientDayNight)
        assertEquals(true, config.showMoonPhase)
        assertEquals(false, config.showGoldenHour)
        assertEquals(false, config.showSolarNoon)
        assertEquals(false, config.redNightMode)
    }

    @Test
    fun testGradientAngleCalculation() {
        // Sunset at 18:00 (90 deg canvas angle) and sunrise at 06:00 (270 deg canvas angle)
        val stops = WatchDialRenderer.calculateGradientStops(
            sunsetHour = 18.0,
            sunriseHour = 6.0,
            dayColor = 0xFFFFFFFF.toInt(),
            nightColor = 0xFF000000.toInt()
        )
        // Check that stops are sorted monotonically in [0f..1f]
        for (i in 0 until stops.positions.size - 1) {
            org.junit.Assert.assertTrue(stops.positions[i] <= stops.positions[i + 1])
        }
        assertEquals(0f, stops.positions.first(), 0.001f)
        assertEquals(1f, stops.positions.last(), 0.001f)
        assertEquals(stops.colors.first(), stops.colors.last())
    }

    @Test
    fun testColorInterpolation() {
        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF000000.toInt()
        assertEquals(white, WatchDialRenderer.interpolateColor(white, black, 0.0f))
        assertEquals(black, WatchDialRenderer.interpolateColor(white, black, 1.0f))
        
        val mid = WatchDialRenderer.interpolateColor(white, black, 0.5f)
        val midR = (mid ushr 16) and 0xFF
        val midG = (mid ushr 8) and 0xFF
        val midB = mid and 0xFF
        assertEquals(128, midR)
        assertEquals(128, midG)
        assertEquals(128, midB)
    }

    @Test
    fun testGradientStopsSummerAndWinter() {
        // Summer day: sunrise 04:00, sunset 22:00
        val summerStops = WatchDialRenderer.calculateGradientStops(
            sunsetHour = 22.0,
            sunriseHour = 4.0,
            dayColor = 0xFF222228.toInt(),
            nightColor = 0xFF08080C.toInt()
        )
        for (i in 0 until summerStops.positions.size - 1) {
            org.junit.Assert.assertTrue(summerStops.positions[i] <= summerStops.positions[i + 1])
        }
        assertEquals(0f, summerStops.positions.first(), 0.001f)
        assertEquals(1f, summerStops.positions.last(), 0.001f)
        assertEquals(summerStops.colors.first(), summerStops.colors.last())

        // Winter day: sunrise 09:00, sunset 15:30
        val winterStops = WatchDialRenderer.calculateGradientStops(
            sunsetHour = 15.5,
            sunriseHour = 9.0,
            dayColor = 0xFF1C2541.toInt(),
            nightColor = 0xFF050814.toInt()
        )
        for (i in 0 until winterStops.positions.size - 1) {
            org.junit.Assert.assertTrue(winterStops.positions[i] <= winterStops.positions[i + 1])
        }
        assertEquals(0f, winterStops.positions.first(), 0.001f)
        assertEquals(1f, winterStops.positions.last(), 0.001f)
        assertEquals(winterStops.colors.first(), winterStops.colors.last())
    }

    @Test
    fun testAstronomicalComplicationsConfig() {
        val config = ClockConfig(
            lat = 55.75,
            lon = 37.61,
            theme = DialTheme.CLASSIC_DARK,
            showUv = true,
            numeralStyle = NumeralStyle.ARABIC,
            numeralOrientation = NumeralOrientation.UPRIGHT,
            numeralDisplayMode = NumeralDisplayMode.ALL,
            fontSizeScale = 1.0f,
            numeralFont = NumeralFont.SANS_SERIF,
            handStyle = HandStyle.BOTTA_NEEDLE,
            bgMode = BackgroundMode.THEME_DEFAULT,
            customColor = 0,
            showMoonPhase = true,
            showGoldenHour = true,
            showSolarNoon = true,
            redNightMode = true
        )
        assertTrue(config.showMoonPhase)
        assertTrue(config.showGoldenHour)
        assertTrue(config.showSolarNoon)
        assertTrue(config.redNightMode)
    }
}
