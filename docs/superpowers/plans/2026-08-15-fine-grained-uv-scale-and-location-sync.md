# Fine-Grained 12-Tier UV Color Scale & Active Location Sync Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement 12 fine-grained 1.0-step UV color tiers in `Uno24DialRenderer.kt`, and active location request with coordinate-keyed Open-Meteo UV cache in `LocationHelper.kt` and `UvRepository.kt`.

**Architecture:** Update `getUvColor` to return 12 discrete color bands. Add location update callback in `LocationHelper`. Key UV cache by `lat,lon,date` in `UvRepository` with callback notification to reload views upon fetch completion.

**Tech Stack:** Kotlin, Android LocationManager, Open-Meteo REST API, Canvas 2D, JUnit 4.

## Global Constraints
- `getUvColor(uvVal: Float)` matches the 12 color hex values defined in the spec.
- Active location request handles permissions gracefully without crashing.
- Always prefix shell commands with `rtk`.

---

### Task 1: 12-Tier UV Color Scale in `Uno24DialRenderer`

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt`
- Test: `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`

- [ ] **Step 1: Write failing test in `Uno24DialRendererTest.kt`**

```kotlin
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest --tests com.uno24.wallpaper.Uno24DialRendererTest.test12TierUvColorScale`
Expected: FAIL.

- [ ] **Step 3: Update `getUvColor` in `Uno24DialRenderer.kt`**

```kotlin
fun getUvColor(uvVal: Float): Int {
    return when {
        uvVal >= 11.5f -> 0xFF6A1B9A.toInt() // 11.5+ (Deep UV Violet)
        uvVal >= 10.5f -> 0xFF9C27B0.toInt() // 10.5..11.4 (Amethyst Purple)
        uvVal >= 9.5f  -> 0xFFD81B60.toInt() // 9.5..10.4 (Ruby Magenta)
        uvVal >= 8.5f  -> 0xFFE53935.toInt() // 8.5..9.4 (Crimson Red)
        uvVal >= 7.5f  -> 0xFFFF5722.toInt() // 7.5..8.4 (Flame Coral)
        uvVal >= 6.5f  -> 0xFFFB8C00.toInt() // 6.5..7.4 (Tangerine Orange)
        uvVal >= 5.5f  -> 0xFFFFB300.toInt() // 5.5..6.4 (Honey Amber)
        uvVal >= 4.5f  -> 0xFFFDD835.toInt() // 4.5..5.4 (Amber Gold)
        uvVal >= 3.5f  -> 0xFFFFEB3B.toInt() // 3.5..4.4 (Lemon Yellow)
        uvVal >= 2.5f  -> 0xFFCDDC39.toInt() // 2.5..3.4 (Chartreuse)
        uvVal >= 1.5f  -> 0xFF8BC34A.toInt() // 1.5..2.4 (Lime)
        uvVal >= 0.5f  -> 0xFF4CAF50.toInt() // 0.5..1.4 (Emerald Green)
        else -> 0
    }
}
```

- [ ] **Step 4: Run unit tests to verify they pass**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt
rtk git commit -m "feat: expand UV color scale to 12 fine-grained 1.0-step tiers"
```

---

### Task 2: Active Location Sync & Coordinate-Keyed UV Cache

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/LocationHelper.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/UvRepository.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24ClockView.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/MainActivity.kt`

- [ ] **Step 1: Enhance `UvRepository.kt` with coordinate/date cache keying and callback**
- [ ] **Step 2: Enhance `LocationHelper.kt` to request active location updates and notify listener**
- [ ] **Step 3: Update `Uno24ClockView.kt` and `MainActivity.kt` to register for location and UV updates**
- [ ] **Step 4: Run unit tests and assembleDebug**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew test assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/
rtk git commit -m "feat: add active GPS location sync and coordinate-keyed Open-Meteo UV caching"
```

---

### Task 3: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [ ] **Step 1: Install APK on emulator**
- [ ] **Step 2: Launch `MainActivity` and verify UV index and 12-tier color arc**
