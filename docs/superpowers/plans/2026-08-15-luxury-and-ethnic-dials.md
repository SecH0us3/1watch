# Luxury & Ethnic Dials Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement 5 authentic ethnic numeral systems (Kanji, Devanagari, Eastern Arabic, Greek, Hebrew) and 5 luxury watch themes (Rose Gold & Onyx, Royal Emerald, Urushi & Cinnabar, Arabian Lapis, Nordic Platinum), with mandatory radial alignment for binary numerals.

**Architecture:** Extend `NumeralStyle` with formatting functions for all 10 numeral systems. Extend `DialTheme` with color palettes for 14 total themes. Update `Uno24DialRenderer` to enforce radial orientation for binary numerals and properly scale all font scripts. Update `MainActivity` and all 12 XML string localization resources.

**Tech Stack:** Kotlin, Android Canvas 2D graphics, Jetpack, JUnit 4, Android Emulator via adb.

## Global Constraints
- `NumeralStyle` supports 10 styles: `ARABIC`, `ROMAN`, `HEXADECIMAL`, `BINARY`, `OCTAL`, `KANJI`, `DEVANAGARI`, `EASTERN_ARABIC`, `GREEK`, `HEBREW`.
- `DialTheme` supports 14 themes: 6 light, 3 dark/cyber, 5 luxury/ethnic (`ROSE_GOLD_ONYX`, `ROYAL_EMERALD`, `URUSHI_JAPAN`, `ARABIAN_LAPIS`, `NORDIC_PLATINUM`).
- `BINARY` numerals must always be oriented radially towards center (`effectiveRadial = isRadial || numeralStyle == NumeralStyle.BINARY`).
- All 12 localization directories must be updated.
- Always prefix shell commands with `rtk`.

---

### Task 1: Extend `NumeralStyle` and Formatting Logic

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/NumeralStyle.kt`
- Test: `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`

**Interfaces:**
- Produces:
  ```kotlin
  enum class NumeralStyle {
      ARABIC, ROMAN, HEXADECIMAL, BINARY, OCTAL,
      KANJI, DEVANAGARI, EASTERN_ARABIC, GREEK, HEBREW;
      fun formatHour(hour: Int): String
      companion object { fun fromName(name: String?): NumeralStyle }
  }
  ```

- [ ] **Step 1: Write the failing test for new NumeralStyles**

In `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`:
```kotlin
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest --tests com.uno24.wallpaper.Uno24DialRendererTest.testEthnicNumeralStyles`
Expected: FAIL (unresolved references for new enum entries).

- [ ] **Step 3: Implement new NumeralStyles in `NumeralStyle.kt`**

```kotlin
package com.uno24.wallpaper

enum class NumeralStyle {
    ARABIC,
    ROMAN,
    HEXADECIMAL,
    BINARY,
    OCTAL,
    KANJI,
    DEVANAGARI,
    EASTERN_ARABIC,
    GREEK,
    HEBREW;

    fun formatHour(hour: Int): String {
        val h = hour.coerceIn(0, 23)
        return when (this) {
            ARABIC -> String.format("%02d", h)
            ROMAN -> toRoman(h)
            HEXADECIMAL -> String.format("%02X", h)
            BINARY -> Integer.toBinaryString(h).padStart(5, '0')
            OCTAL -> String.format("%02o", h)
            KANJI -> toKanji(h)
            DEVANAGARI -> toDevanagari(h)
            EASTERN_ARABIC -> toEasternArabic(h)
            GREEK -> toGreek(h)
            HEBREW -> toHebrew(h)
        }
    }

    companion object {
        fun fromName(name: String?): NumeralStyle {
            return entries.firstOrNull { it.name == name } ?: ARABIC
        }

        private fun toRoman(hour: Int): String {
            val num = if (hour == 0) 24 else hour
            val romanMap = listOf(
                10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
            )
            val sb = StringBuilder()
            var remainder = num
            for ((value, symbol) in romanMap) {
                while (remainder >= value) {
                    sb.append(symbol)
                    remainder -= value
                }
            }
            return sb.toString()
        }

        private fun toKanji(hour: Int): String {
            if (hour == 0) return "〇"
            val digits = arrayOf("〇", "一", "二", "三", "四", "五", "六", "七", "八", "九")
            return when {
                hour < 10 -> digits[hour]
                hour == 10 -> "十"
                hour < 20 -> "十" + digits[hour % 10]
                hour == 20 -> "二十"
                else -> "二十" + digits[hour % 10]
            }
        }

        private fun toDevanagari(hour: Int): String {
            val devanagariDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
            val s = String.format("%02d", hour)
            return s.map { devanagariDigits[it - '0'] }.joinToString("")
        }

        private fun toEasternArabic(hour: Int): String {
            val arabicIndicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            val s = String.format("%02d", hour)
            return s.map { arabicIndicDigits[it - '0'] }.joinToString("")
        }

        private fun toGreek(hour: Int): String {
            val h = if (hour == 0) 24 else hour
            val greekOnes = arrayOf("", "Α", "Β", "Γ", "Δ", "Ε", "Ϛ", "Ζ", "Η", "Θ")
            val greekTens = arrayOf("", "Ι", "Κ")
            val tens = h / 10
            val ones = h % 10
            return "${greekTens[tens]}${greekOnes[ones]}ʹ"
        }

        private fun toHebrew(hour: Int): String {
            val h = if (hour == 0) 24 else hour
            return when (h) {
                1 -> "א׳"
                2 -> "ב׳"
                3 -> "ג׳"
                4 -> "ד׳"
                5 -> "ה׳"
                6 -> "ו׳"
                7 -> "ז׳"
                8 -> "ח׳"
                9 -> "ט׳"
                10 -> "י׳"
                11 -> "י״א"
                12 -> "י״ב"
                13 -> "י״ג"
                14 -> "י״ד"
                15 -> "ט״ו"
                16 -> "ט״ז"
                17 -> "י״ז"
                18 -> "י״ח"
                19 -> "י״ט"
                20 -> "כ׳"
                21 -> "כ״א"
                22 -> "כ״ב"
                23 -> "כ״ג"
                24 -> "כ״ד"
                else -> "כ״ד"
            }
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/NumeralStyle.kt app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt
rtk git commit -m "feat: add ethnic numeral systems (Kanji, Devanagari, Eastern Arabic, Greek, Hebrew)"
```

---

### Task 2: Extend `DialTheme` with 5 Luxury & Ethnic Themes

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/DialTheme.kt`
- Test: `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`

**Interfaces:**
- Produces: 5 new entries in `DialTheme`: `ROSE_GOLD_ONYX`, `ROYAL_EMERALD`, `URUSHI_JAPAN`, `ARABIAN_LAPIS`, `NORDIC_PLATINUM`.

- [ ] **Step 1: Write the failing test for new DialThemes**

In `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`:
```kotlin
@Test
fun testLuxuryDialThemes() {
    assertEquals(DialTheme.ROSE_GOLD_ONYX, DialTheme.fromName("ROSE_GOLD_ONYX"))
    assertEquals(DialTheme.ROYAL_EMERALD, DialTheme.fromName("ROYAL_EMERALD"))
    assertEquals(DialTheme.URUSHI_JAPAN, DialTheme.fromName("URUSHI_JAPAN"))
    assertEquals(DialTheme.ARABIAN_LAPIS, DialTheme.fromName("ARABIAN_LAPIS"))
    assertEquals(DialTheme.NORDIC_PLATINUM, DialTheme.fromName("NORDIC_PLATINUM"))
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest --tests com.uno24.wallpaper.Uno24DialRendererTest.testLuxuryDialThemes`
Expected: FAIL.

- [ ] **Step 3: Implement new themes in `DialTheme.kt`**

Add the 5 themes with tailored colors to `DialTheme.kt`.

- [ ] **Step 4: Run test to verify it passes**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/DialTheme.kt app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt
rtk git commit -m "feat: add 5 luxury & ethnic dial themes"
```

---

### Task 3: Enforce Binary Radial Orientation and Font Sizing in `Uno24DialRenderer`

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt`

- [ ] **Step 1: Update `isRadial` check and sizing in `Uno24DialRenderer.kt`**

- Enforce `val isRadial = numeralOrientation == NumeralOrientation.RADIAL || numeralStyle == NumeralStyle.BINARY`.
- Adjust `baseSize` calculation:
  ```kotlin
  val baseSize = when {
      numeralStyle == NumeralStyle.BINARY -> if (numeralDisplayMode == NumeralDisplayMode.ALL) radius * 0.046f else radius * 0.055f
      numeralStyle == NumeralStyle.KANJI || numeralStyle == NumeralStyle.GREEK || numeralStyle == NumeralStyle.HEBREW -> radius * 0.062f
      numeralStyle == NumeralStyle.ROMAN -> radius * 0.068f
      numeralDisplayMode == NumeralDisplayMode.ALL -> radius * 0.065f
      else -> radius * 0.082f
  }
  ```
- Use `isRadial` when drawing numerals:
  ```kotlin
  if (isRadial) {
      canvas.save()
      canvas.rotate(angle + 90f, labelX, labelY)
      canvas.drawText(labelText, labelX, labelY + textVerticalOffset, textPaint)
      canvas.restore()
  } else {
      canvas.drawText(labelText, labelX, labelY + textVerticalOffset, textPaint)
  }
  ```

- [ ] **Step 2: Run tests to verify compilation and execution**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew test`
Expected: PASS.

- [ ] **Step 3: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt
rtk git commit -m "feat: enforce radial orientation for binary numerals and optimize script sizing"
```

---

### Task 4: UI Spinner Binding and All 12 Language Localizations

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/MainActivity.kt`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-ru/strings.xml`
- Modify: `app/src/main/res/values-es/strings.xml`
- Modify: `app/src/main/res/values-de/strings.xml`
- Modify: `app/src/main/res/values-fr/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`
- Modify: `app/src/main/res/values-ja/strings.xml`
- Modify: `app/src/main/res/values-hi/strings.xml`
- Modify: `app/src/main/res/values-la/strings.xml`
- Modify: `app/src/main/res/values-el/strings.xml`
- Modify: `app/src/main/res/values-b+ang/strings.xml`

- [ ] **Step 1: Update strings in all 12 localization files**

Add string definitions for:
- `numeral_style_kanji`, `numeral_style_devanagari`, `numeral_style_eastern_arabic`, `numeral_style_greek`, `numeral_style_hebrew`
- `theme_rose_gold_onyx`, `theme_royal_emerald`, `theme_urushi_japan`, `theme_arabian_lapis`, `theme_nordic_platinum`

- [ ] **Step 2: Update `MainActivity.kt` spinner bindings**

Update `numeralStyles` and `themes` lists in `MainActivity.kt` to include all new options.

- [ ] **Step 3: Run `./gradlew test assembleDebug`**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew test assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/MainActivity.kt app/src/main/res/values*
rtk git commit -m "feat: add UI bindings and localizations for 10 numeral systems and 14 themes"
```

---

### Task 5: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [ ] **Step 1: Install updated APK on Android emulator**

Run: `rtk orca emulator install ./app/build/outputs/apk/debug/app-debug.apk --reinstall --device emulator-5554 --json`

- [ ] **Step 2: Launch `MainActivity` and test themes and numeral styles**

Run: `rtk orca emulator launch com.uno24.wallpaper --activity .MainActivity --device emulator-5554 --json`

- [ ] **Step 3: Capture screenshots and visually verify**

Capture screenshots of:
1. Binary numerals with enforced radial orientation
2. Kanji numerals (`〇`..`二十三`) with Urushi & Cinnabar theme
3. Eastern Arabic numerals (`٠٠`..`٢٣`) with Arabian Lapis theme
4. Devanagari numerals (`००`..`२३`) with Royal Emerald theme
5. Greek numerals (`Αʹ`..`ΚΔʹ`) with Rose Gold & Onyx theme
6. Hebrew numerals (`א׳`..`כ״ד`) with Nordic Platinum theme

- [ ] **Step 4: Commit all final adjustments**

```bash
rtk git commit -a -m "test: verify luxury themes and ethnic numeral systems on emulator"
```
