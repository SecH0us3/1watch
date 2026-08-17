package com.uno24.wallpaper

import android.graphics.Color
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

    @Test
    fun testTimeToAngleQuarterHours() {
        // 12:15 = 12.25h -> 0.25 * 15 = 3.75 deg
        assertEquals(3.75f, Uno24DialRenderer.timeToAngle(12.25), 0.01f)
        // 12:30 = 12.5h -> 0.5 * 15 = 7.5 deg
        assertEquals(7.5f, Uno24DialRenderer.timeToAngle(12.5), 0.01f)
        // 12:45 = 12.75h -> 0.75 * 15 = 11.25 deg
        assertEquals(11.25f, Uno24DialRenderer.timeToAngle(12.75), 0.01f)
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
        assertEquals(0, Uno24DialRenderer.getUvColor(0.0f))
        assertEquals(0, Uno24DialRenderer.getUvColor(0.4f))
        assertEquals(0xFF4CAF50.toInt(), Uno24DialRenderer.getUvColor(1.0f))  // 0.5..1.4 (Emerald Green)
        assertEquals(0xFF8BC34A.toInt(), Uno24DialRenderer.getUvColor(2.0f))  // 1.5..2.4 (Lime)
        assertEquals(0xFFCDDC39.toInt(), Uno24DialRenderer.getUvColor(3.0f))  // 2.5..3.4 (Chartreuse)
        assertEquals(0xFFFFEB3B.toInt(), Uno24DialRenderer.getUvColor(4.0f))  // 3.5..4.4 (Lemon Yellow)
        assertEquals(0xFFFDD835.toInt(), Uno24DialRenderer.getUvColor(5.0f))  // 4.5..5.4 (Amber Gold)
        assertEquals(0xFFFFB300.toInt(), Uno24DialRenderer.getUvColor(6.0f))  // 5.5..6.4 (Honey Amber)
        assertEquals(0xFFFB8C00.toInt(), Uno24DialRenderer.getUvColor(7.0f))  // 6.5..7.4 (Tangerine Orange)
        assertEquals(0xFFFF5722.toInt(), Uno24DialRenderer.getUvColor(8.0f))  // 7.5..8.4 (Flame Coral - Paphos UV 8)
        assertEquals(0xFFE53935.toInt(), Uno24DialRenderer.getUvColor(9.0f))  // 8.5..9.4 (Crimson Red)
        assertEquals(0xFFD81B60.toInt(), Uno24DialRenderer.getUvColor(10.0f)) // 9.5..10.4 (Ruby Magenta)
        assertEquals(0xFF9C27B0.toInt(), Uno24DialRenderer.getUvColor(11.0f)) // 10.5..11.4 (Amethyst Purple)
        assertEquals(0xFF6A1B9A.toInt(), Uno24DialRenderer.getUvColor(12.0f)) // 11.5+ (Deep UV Violet)
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
}
