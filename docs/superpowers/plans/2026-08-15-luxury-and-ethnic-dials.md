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

- [x] **Step 1: Write the failing test for new NumeralStyles**
- [x] **Step 2: Run test to verify it fails**
- [x] **Step 3: Implement new NumeralStyles in `NumeralStyle.kt`**
- [x] **Step 4: Run tests to verify they pass**
- [x] **Step 5: Commit**

---

### Task 2: Extend `DialTheme` with 5 Luxury & Ethnic Themes

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/DialTheme.kt`
- Test: `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`

**Interfaces:**
- Produces: 5 new entries in `DialTheme`: `ROSE_GOLD_ONYX`, `ROYAL_EMERALD`, `URUSHI_JAPAN`, `ARABIAN_LAPIS`, `NORDIC_PLATINUM`.

- [x] **Step 1: Write the failing test for new DialThemes**
- [x] **Step 2: Run test to verify it fails**
- [x] **Step 3: Implement new themes in `DialTheme.kt`**
- [x] **Step 4: Run test to verify it passes**
- [x] **Step 5: Commit**

---

### Task 3: Enforce Binary Radial Orientation and Font Sizing in `Uno24DialRenderer`

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt`

- [x] **Step 1: Update `isRadial` check and sizing in `Uno24DialRenderer.kt`**
- [x] **Step 2: Run tests to verify compilation and execution**
- [x] **Step 3: Commit**

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

- [x] **Step 1: Update strings in all 12 localization files**
- [x] **Step 2: Update `MainActivity.kt` spinner bindings**
- [x] **Step 3: Run `./gradlew test assembleDebug`**
- [x] **Step 4: Commit**

---

### Task 5: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [x] **Step 1: Install updated APK on Android emulator**
- [x] **Step 2: Launch `MainActivity` and test themes and numeral styles**
- [x] **Step 3: Capture screenshots and visually verify**
- [x] **Step 4: Commit all final adjustments**
