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
}
