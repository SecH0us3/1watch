# UNO 24 Refinements & Optimizations Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Elevate the UNO 24 wallpaper and preview app to production-grade quality: add authentic 15-minute sub-hour dial markings, eliminate memory allocations and disk I/O in render loops, reduce wallpaper battery consumption by 15x-30x, support polar day/night edge cases, add custom color picker support, and polish the settings UI with dedicated vector icons.

**Architecture:** 
- `Uno24DialRenderer`: Add 96-step sub-hour subdivisions (15-min ticks), fontMetrics-based baseline text alignment, pre-cached string tables, and reusable `RectF` buffers.
- `SolarCalculator`: Add explicit `isPolarDay` and `isPolarNight` detection to handle high-latitude astronomical edge cases cleanly.
- `Uno24WallpaperService` & `Uno24ClockView`: Migrate from polling SharedPreferences/disk in `onDraw` to in-memory state caching with `SharedPreferences.OnSharedPreferenceChangeListener` and intelligent 15-30s power-efficient frame scheduling.
- `MainActivity` & Resources: Add custom vector icons (`ic_minus`, `ic_plus`), a modern preset color picker for `BackgroundMode.CUSTOM_COLOR`, and polish UI layouts.

**Tech Stack:** Kotlin, Android Canvas / 2D Graphics, Material Components, SharedPreferences, Vector Drawables.

---

### Task 1: Add Sub-Hour Ticks, GC-Free Buffering & FontMetrics to `Uno24DialRenderer`

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt`
- Modify: `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`

**Interfaces:**
- `Uno24DialRenderer`:
  - Consumes: `SunTimes`, `DialTheme`, `NumeralStyle`, `NumeralOrientation`, `NumeralDisplayMode`, `NumeralFont`, `BackgroundMode`.
  - Renders 96 ticks per 24 hours: Major (even hour), Sub-major (odd hour), Half-hour (xx:30), Quarter-hour (xx:15, xx:45).
  - Uses preallocated string lookup tables `ARABIC_STRINGS` and `ROMAN_STRINGS` and reusable `RectF` bounds.

- [ ] **Step 1: Update `Uno24DialRendererTest.kt` with tests for quarter-hour angles and tick mathematics**
- [ ] **Step 2: Update `Uno24DialRenderer.kt` with zero-allocation buffers, 15-minute subdivision ticks, and FontMetrics centering**
- [ ] **Step 3: Verify math calculations in `Uno24DialRenderer`**
- [ ] **Step 4: Commit changes**

---

### Task 2: Enhance `SolarCalculator` with Polar Day and Polar Night Edge Cases

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/SolarCalculator.kt`
- Modify: `app/src/test/java/com/uno24/wallpaper/SolarCalculatorTest.kt`

**Interfaces:**
- `SunTimes(sunriseHour: Double, sunsetHour: Double, isPolarDay: Boolean = false, isPolarNight: Boolean = false)`
- Handles `cosHourAngle < -1.0` (midnight sun / polar day) and `cosHourAngle > 1.0` (polar night).

- [ ] **Step 1: Update `SolarCalculatorTest.kt` to include polar day/night assertions**
- [ ] **Step 2: Update `SolarCalculator.kt` with polar flags and integrate into `Uno24DialRenderer.kt`**
- [ ] **Step 3: Commit changes**

---

### Task 3: Optimize Battery and Frame Scheduling in `Uno24WallpaperService` & `Uno24ClockView`

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24WallpaperService.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24ClockView.kt`

**Interfaces:**
- Move all `LocationHelper.get*` calls out of `drawFrame()` / `onDraw()`.
- Implement `SharedPreferences.OnSharedPreferenceChangeListener` to update cached configuration state in memory.
- In `Uno24WallpaperService`, calculate frame delay targeting the next 15-second / minute boundary for battery efficiency.

- [ ] **Step 1: Implement cached settings model in `Uno24WallpaperService` and `Uno24ClockView`**
- [ ] **Step 2: Update frame scheduler to use power-efficient intervals (15s on wallpaper)**
- [ ] **Step 3: Commit changes**

---

### Task 4: UI Polish — Vector Icons, Color Palette Picker & Layout Enhancements

**Files:**
- Create: `app/src/main/res/drawable/ic_minus.xml`
- Create: `app/src/main/res/drawable/ic_plus.xml`
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/java/com/uno24/wallpaper/MainActivity.kt`

**Interfaces:**
- Add modern vector minus/plus icons for font size controls.
- Add preset color selection dialog/bottom sheet for `BackgroundMode.CUSTOM_COLOR`.
- Refresh theme and preview immediately upon preference change.

- [ ] **Step 1: Create `ic_minus.xml` and `ic_plus.xml` vector assets**
- [ ] **Step 2: Update `activity_main.xml` to use the new vector assets and ensure seamless layout**
- [ ] **Step 3: Update `MainActivity.kt` with custom color dialog and immediate reactive sync**
- [ ] **Step 4: Commit changes**
