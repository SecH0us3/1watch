# Astronomical Complications & Red Cockpit Night Mode Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Moon Phase indicator, Golden/Blue hour rim micro-arcs, True Solar Noon marker, and Red Cockpit Night Mode in 1watch without adding any external dependencies or raster image assets.

**Architecture:**
- `MoonCalculator`: Pure math synodic cycle algorithm ($29.530588853$ days) calculating normalized phase $P \in [0.0, 1.0)$ and illumination fraction.
- `SolarCalculator`: Enhanced to compute Solar Noon ($720 - 4\lambda - \text{eqTime} + \text{tzOffset}\cdot 60$) and Golden/Blue hour intervals based on solar zenith angles ($84^\circ$ and $96^\circ$).
- `WatchDialRenderer`: Direct Android `Canvas` vector drawing for Moon disk, Golden/Blue micro-arcs, Solar Noon glyph, and dynamic red/crimson cockpit color tone mapping.
- `LocationHelper` & `activity_main.xml`: Persistence in `SharedPreferences`, `ClockConfig` data class, and Material switch controls with full 11-locale translations.

**Tech Stack:** Kotlin, Android Canvas/Paint/Path 2D Graphics, JUnit 4, Material Design Components.

## Global Constraints
- Target Android SDK 36, Min SDK 26, JVM 17.
- Zero external libraries or image files added.
- All shell commands must be prefixed with `rtk` (e.g. `rtk git`, `rtk ./gradlew`).
- Gradle execution environment: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew ...`

---

### Task 1: Moon Phase Calculator (`MoonCalculator.kt`) & Unit Tests

**Files:**
- Create: `app/src/main/java/com/watch1/app/MoonCalculator.kt`
- Create: `app/src/test/java/com/watch1/app/MoonCalculatorTest.kt`

**Interfaces:**
- Produces: `MoonCalculator.calculateMoonInfo(date: LocalDate): MoonInfo` where `MoonInfo(val phase: Double, val illumination: Double, val phaseName: MoonPhaseName)` and `enum class MoonPhaseName`.

- [ ] **Step 1: Write the unit tests for MoonCalculator**
Create `app/src/test/java/com/watch1/app/MoonCalculatorTest.kt`:
```kotlin
package com.watch1.app

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class MoonCalculatorTest {
    @Test
    fun testKnownNewMoon() {
        // Known New Moon: 2024-01-11
        val info = MoonCalculator.calculateMoonInfo(LocalDate.of(2024, 1, 11))
        assertTrue("Phase should be near 0.0 or 1.0 on new moon", info.phase < 0.05 || info.phase > 0.95)
        assertTrue("Illumination should be near 0% on new moon", info.illumination < 0.1)
    }

    @Test
    fun testKnownFullMoon() {
        // Known Full Moon: 2024-01-25
        val info = MoonCalculator.calculateMoonInfo(LocalDate.of(2024, 1, 25))
        assertTrue("Phase should be near 0.5 on full moon", info.phase in 0.45..0.55)
        assertTrue("Illumination should be near 100% on full moon", info.illumination > 0.9)
    }

    @Test
    fun testPhaseMonotonicity() {
        var prevPhase = MoonCalculator.calculateMoonInfo(LocalDate.of(2026, 1, 1)).phase
        for (i in 2..28) {
            val currPhase = MoonCalculator.calculateMoonInfo(LocalDate.of(2026, 1, i)).phase
            // As days increase within a single cycle, phase should increase
            if (currPhase > prevPhase) {
                assertTrue(currPhase >= prevPhase)
            }
            prevPhase = currPhase
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**
Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests com.watch1.app.MoonCalculatorTest`
Expected: FAIL (Unresolved reference: MoonCalculator).

- [ ] **Step 3: Implement MoonCalculator.kt**
Create `app/src/main/java/com/watch1/app/MoonCalculator.kt`:
```kotlin
package com.watch1.app

import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.cos
import kotlin.math.PI

enum class MoonPhaseName {
    NEW_MOON,
    WAXING_CRESCENT,
    FIRST_QUARTER,
    WAXING_GIBBOUS,
    FULL_MOON,
    WANING_GIBBOUS,
    LAST_QUARTER,
    WANING_CRESCENT
}

data class MoonInfo(
    val phase: Double, // 0.0..1.0
    val illumination: Double, // 0.0..1.0 (0% to 100%)
    val phaseName: MoonPhaseName
)

object MoonCalculator {
    private const val SYNODIC_MONTH = 29.530588853
    // Reference New Moon epoch: 2024-01-11 11:57 UTC = 1704974220 epoch seconds
    private const val REFERENCE_NEW_MOON_EPOCH_SEC = 1704974220L

    fun calculateMoonInfo(date: LocalDate): MoonInfo {
        val epochSeconds = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond() + 43200 // Noon UTC
        val diffDays = (epochSeconds - REFERENCE_NEW_MOON_EPOCH_SEC).toDouble() / 86400.0
        val cycles = diffDays / SYNODIC_MONTH
        val phase = (cycles - kotlin.math.floor(cycles)).mod(1.0)

        // Illumination fraction k = (1 - cos(2*pi*phase)) / 2
        val illumination = (1.0 - cos(2.0 * PI * phase)) / 2.0

        val name = when {
            phase < 0.03 || phase >= 0.97 -> MoonPhaseName.NEW_MOON
            phase < 0.22 -> MoonPhaseName.WAXING_CRESCENT
            phase < 0.28 -> MoonPhaseName.FIRST_QUARTER
            phase < 0.47 -> MoonPhaseName.WAXING_GIBBOUS
            phase < 0.53 -> MoonPhaseName.FULL_MOON
            phase < 0.72 -> MoonPhaseName.WANING_GIBBOUS
            phase < 0.78 -> MoonPhaseName.LAST_QUARTER
            else -> MoonPhaseName.WANING_CRESCENT
        }

        return MoonInfo(phase, illumination, name)
    }
}
```

- [ ] **Step 4: Run test to verify it passes**
Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests com.watch1.app.MoonCalculatorTest`
Expected: PASS.

- [ ] **Step 5: Commit Task 1**
```bash
rtk git add app/src/main/java/com/watch1/app/MoonCalculator.kt app/src/test/java/com/watch1/app/MoonCalculatorTest.kt
rtk git commit -m "feat: implement MoonCalculator with synodic phase and illumination algorithm"
```

---

### Task 2: Extend `SolarCalculator.kt` for Golden/Blue Hours & Solar Noon

**Files:**
- Modify: `app/src/main/java/com/watch1/app/SolarCalculator.kt`
- Create: `app/src/test/java/com/watch1/app/SolarCalculatorTest.kt`

**Interfaces:**
- Consumes: `LocalDate`, `latitude: Double`, `longitude: Double`, `timeZoneOffsetHours: Double`.
- Produces: `SunTimes` with `solarNoonHour: Double`, `morningBlueHourStart: Double?`, `morningGoldenHourEnd: Double?`, `eveningGoldenHourStart: Double?`, `eveningBlueHourEnd: Double?`.

- [ ] **Step 1: Write tests for extended SolarCalculator in `SolarCalculatorTest.kt`**
Create `app/src/test/java/com/watch1/app/SolarCalculatorTest.kt`:
```kotlin
package com.watch1.app

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class SolarCalculatorTest {
    @Test
    fun testSolarNoonCalculation() {
        // Moscow: 55.75 N, 37.61 E, UTC+3
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 55.75,
            longitude = 37.61,
            date = LocalDate.of(2026, 6, 21),
            timeZoneOffsetHours = 3.0
        )
        // Solar noon should be around 12:30 (12.5h) in Moscow
        assertEquals(12.5, sunTimes.solarNoonHour, 0.5)
        assertTrue(sunTimes.sunriseHour < sunTimes.solarNoonHour)
        assertTrue(sunTimes.sunsetHour > sunTimes.solarNoonHour)
    }

    @Test
    fun testGoldenAndBlueHours() {
        val sunTimes = SolarCalculator.calculateSunTimes(
            latitude = 55.75,
            longitude = 37.61,
            date = LocalDate.of(2026, 6, 21),
            timeZoneOffsetHours = 3.0
        )
        // Morning blue hour ends at sunrise
        assertNotNull(sunTimes.morningBlueHourStart)
        assertTrue(sunTimes.morningBlueHourStart!! < sunTimes.sunriseHour)

        // Morning golden hour starts at sunrise and ends after sunrise
        assertNotNull(sunTimes.morningGoldenHourEnd)
        assertTrue(sunTimes.morningGoldenHourEnd!! > sunTimes.sunriseHour)

        // Evening golden hour starts before sunset and ends at sunset
        assertNotNull(sunTimes.eveningGoldenHourStart)
        assertTrue(sunTimes.eveningGoldenHourStart!! < sunTimes.sunsetHour)

        // Evening blue hour starts at sunset and ends after sunset
        assertNotNull(sunTimes.eveningBlueHourEnd)
        assertTrue(sunTimes.eveningBlueHourEnd!! > sunTimes.sunsetHour)
    }
}
```

- [ ] **Step 2: Update `SolarCalculator.kt`**
Update `app/src/main/java/com/watch1/app/SolarCalculator.kt` to calculate zenith angles for $84.0^\circ$ and $96.0^\circ$ and solar noon:
```kotlin
package com.watch1.app

import java.time.LocalDate
import kotlin.math.*

data class SunTimes(
    val sunriseHour: Double,
    val sunsetHour: Double,
    val solarNoonHour: Double = 12.0,
    val morningBlueHourStart: Double? = null,
    val morningGoldenHourEnd: Double? = null,
    val eveningGoldenHourStart: Double? = null,
    val eveningBlueHourEnd: Double? = null,
    val isPolarDay: Boolean = false,
    val isPolarNight: Boolean = false
)

object SolarCalculator {
    fun calculateSunTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        timeZoneOffsetHours: Double
    ): SunTimes {
        val dayOfYear = date.dayOfYear
        val gamma = 2.0 * PI / 365.0 * (dayOfYear - 1 + (12.0 - 12.0) / 24.0)
        
        // Equation of time (minutes)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) - 0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))
        
        // Solar declination (radians)
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) - 0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma) - 0.002697 * cos(3 * gamma) + 0.00148 * sin(3 * gamma)

        val latRad = Math.toRadians(latitude)
        
        // Solar Noon
        val solarNoonMinutes = 720.0 - 4.0 * longitude - eqTime + (timeZoneOffsetHours * 60.0)
        val solarNoonHour = (solarNoonMinutes / 60.0).mod(24.0)

        // Helper to calculate event hours for a given zenith angle
        fun calculateHourAngle(zenithDeg: Double): Double? {
            val zenith = Math.toRadians(zenithDeg)
            val cosHA = (cos(zenith) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
            if (cosHA < -1.0 || cosHA > 1.0) return null
            return Math.toDegrees(acos(cosHA))
        }

        val officialHA = calculateHourAngle(90.833)
        if (officialHA == null) {
            val zenith = Math.toRadians(90.833)
            val cosHA = (cos(zenith) / (cos(latRad) * cos(decl))) - (tan(latRad) * tan(decl))
            if (cosHA < -1.0) {
                return SunTimes(sunriseHour = 0.0, sunsetHour = 24.0, solarNoonHour = solarNoonHour, isPolarDay = true, isPolarNight = false)
            } else {
                return SunTimes(sunriseHour = 12.0, sunsetHour = 12.0, solarNoonHour = solarNoonHour, isPolarDay = false, isPolarNight = true)
            }
        }

        val sunriseMinutes = 720.0 - 4.0 * (longitude + officialHA) - eqTime + (timeZoneOffsetHours * 60.0)
        val sunsetMinutes = 720.0 - 4.0 * (longitude - officialHA) - eqTime + (timeZoneOffsetHours * 60.0)
        val sunriseHour = (sunriseMinutes / 60.0).mod(24.0)
        val sunsetHour = (sunsetMinutes / 60.0).mod(24.0)

        // Civil twilight (Blue Hour): zenith = 96.0 deg
        val civilHA = calculateHourAngle(96.0)
        val morningBlueHourStart = civilHA?.let { ha -> ((720.0 - 4.0 * (longitude + ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }
        val eveningBlueHourEnd = civilHA?.let { ha -> ((720.0 - 4.0 * (longitude - ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }

        // Golden hour: zenith = 84.0 deg
        val goldenHA = calculateHourAngle(84.0)
        val morningGoldenHourEnd = goldenHA?.let { ha -> ((720.0 - 4.0 * (longitude + ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }
        val eveningGoldenHourStart = goldenHA?.let { ha -> ((720.0 - 4.0 * (longitude - ha) - eqTime + timeZoneOffsetHours * 60.0) / 60.0).mod(24.0) }

        return SunTimes(
            sunriseHour = sunriseHour,
            sunsetHour = sunsetHour,
            solarNoonHour = solarNoonHour,
            morningBlueHourStart = morningBlueHourStart,
            morningGoldenHourEnd = morningGoldenHourEnd,
            eveningGoldenHourStart = eveningGoldenHourStart,
            eveningBlueHourEnd = eveningBlueHourEnd,
            isPolarDay = false,
            isPolarNight = false
        )
    }
}
```

- [ ] **Step 3: Run test to verify it passes**
Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests com.watch1.app.SolarCalculatorTest`
Expected: PASS.

- [ ] **Step 4: Commit Task 2**
```bash
rtk git add app/src/main/java/com/watch1/app/SolarCalculator.kt app/src/test/java/com/watch1/app/SolarCalculatorTest.kt
rtk git commit -m "feat: extend SolarCalculator with solar noon and golden/blue hour intervals"
```

---

### Task 3: Localized Strings for the 4 New Switches

**Files:**
- Modify: `app/src/main/res/values*/strings.xml` (all 11 locales)

**Keys to add:**
- `switch_show_moon_phase`
- `switch_show_golden_hour`
- `switch_show_solar_noon`
- `switch_red_night_mode`

- [ ] **Step 1: Add string resources across all 11 languages**
  - `values/strings.xml` (EN):
    ```xml
    <string name="switch_show_moon_phase">Moon phase indicator</string>
    <string name="switch_show_golden_hour">Golden &amp; blue hours</string>
    <string name="switch_show_solar_noon">True solar noon marker</string>
    <string name="switch_red_night_mode">Red night mode (Cockpit)</string>
    ```
  - `values-ru/strings.xml` (RU):
    ```xml
    <string name="switch_show_moon_phase">Индикатор фазы Луны</string>
    <string name="switch_show_golden_hour">«Золотой» и «синий» час</string>
    <string name="switch_show_solar_noon">Метка истинного полудня</string>
    <string name="switch_red_night_mode">Красный ночной режим (Cockpit)</string>
    ```
  - Add corresponding accurate translations in `values-de`, `values-fr`, `values-es`, `values-zh-rCN`, `values-ja`, `values-hi`, `values-el`, `values-la`, `values-b+ang`.

- [ ] **Step 2: Commit Task 3**
```bash
rtk git add app/src/main/res/values*/strings.xml
rtk git commit -m "feat: add localized strings for moon phase, golden hour, solar noon, and red night mode"
```

---

### Task 4: Preferences and `ClockConfig` Extensions in `LocationHelper.kt`

**Files:**
- Modify: `app/src/main/java/com/watch1/app/LocationHelper.kt`
- Modify: `app/src/test/java/com/watch1/app/WatchDialRendererTest.kt`

**Interfaces:**
- Produces: `ClockConfig.showMoonPhase`, `ClockConfig.showGoldenHour`, `ClockConfig.showSolarNoon`, `ClockConfig.redNightMode` and getter/setter functions in `LocationHelper`.

- [ ] **Step 1: Update ClockConfig and LocationHelper**
Add fields to `ClockConfig`:
```kotlin
data class ClockConfig(
    ...
    val showMoonPhase: Boolean = true,
    val showGoldenHour: Boolean = false,
    val showSolarNoon: Boolean = false,
    val redNightMode: Boolean = false
)
```
Add constants and methods in `LocationHelper`:
- `KEY_SHOW_MOON_PHASE = "key_show_moon_phase"` (default `true`)
- `KEY_SHOW_GOLDEN_HOUR = "key_show_golden_hour"` (default `false`)
- `KEY_SHOW_SOLAR_NOON = "key_show_solar_noon"` (default `false`)
- `KEY_RED_NIGHT_MODE = "key_red_night_mode"` (default `false`)
- `getShowMoonPhase(context): Boolean`, `saveShowMoonPhase(context, enabled: Boolean)`
- `getShowGoldenHour(context): Boolean`, `saveShowGoldenHour(context, enabled: Boolean)`
- `getShowSolarNoon(context): Boolean`, `saveShowSolarNoon(context, enabled: Boolean)`
- `getRedNightMode(context): Boolean`, `saveRedNightMode(context, enabled: Boolean)`
- Update `loadConfig(context)`.

- [ ] **Step 2: Verify in Unit Tests and Run**
Update `WatchDialRendererTest.kt` and run:
`JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 3: Commit Task 4**
```bash
rtk git add app/src/main/java/com/watch1/app/LocationHelper.kt app/src/test/java/com/watch1/app/WatchDialRendererTest.kt
rtk git commit -m "feat: add SharedPreferences persistence and ClockConfig fields for new complications"
```

---

### Task 5: Vector Canvas Rendering of Moon Phase, Golden/Blue Arcs, Solar Noon, and Red Night Mode (`WatchDialRenderer.kt`)

**Files:**
- Modify: `app/src/main/java/com/watch1/app/WatchDialRenderer.kt`
- Modify: `app/src/main/java/com/watch1/app/WatchClockView.kt`
- Modify: `app/src/main/java/com/watch1/app/WatchWallpaperService.kt`
- Modify: `app/src/main/java/com/watch1/app/WatchAppWidgetProvider.kt`

**Interfaces:**
- Consumes: `ClockConfig`, `SunTimes`, `MoonInfo`, `date: LocalDate`.
- Produces: Vector rendering of Moon Phase, Golden/Blue arcs, Solar Noon marker, and dynamic Cockpit Red color palette on Canvas.

- [ ] **Step 1: Implement vector drawing in `WatchDialRenderer.kt`**
1. **Red Cockpit Night Mode tone-mapping**:
   If `config.redNightMode` is true and `!isDaytime`:
   - Override theme colors: Background `#080101`, Day sector `#140202`, Night sector `#050000`, Ring `#330A0A`, Ticks `#D32F2F`, Hand `#FF1744`, Numerals `#EF5350`.
2. **Golden & Blue Hour Arcs**:
   When `config.showGoldenHour` is true:
   - Draw morning/evening golden hour arc (`#FFB300`) and blue hour arc (`#0288D1`) using `canvas.drawArc()` along dial radius $r \cdot 0.88$, stroke width $2.5\text{ dp}$.
3. **Solar Noon Marker**:
   When `config.showSolarNoon` is true:
   - Calculate solar noon angle $\theta = \text{timeToAngle}(\text{sunTimes.solarNoonHour}) - 90^\circ$.
   - Draw a golden dot at radius $r \cdot 0.84$ with a subtle sun ray tick / glyph.
4. **Moon Phase Complication**:
   When `config.showMoonPhase` is true:
   - Compute `moonInfo = MoonCalculator.calculateMoonInfo(date)`.
   - Center at $(cx, cy + r \cdot 0.46)$, radius $r_{moon} \approx 9\text{ dp}$.
   - Draw dark moon base circle.
   - Draw illuminated phase using dual arc `Path` (waxing/waning terminator).
   - Draw subtle illumination percentage text (e.g. `78%`) below the moon icon.

- [ ] **Step 2: Propagate parameters into callers**
Update `renderer.draw(...)` calls in `WatchClockView.kt`, `WatchWallpaperService.kt`, `WatchAppWidgetProvider.kt` to pass the new `config` properties.

- [ ] **Step 3: Run unit tests**
Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 4: Commit Task 5**
```bash
rtk git add app/src/main/java/com/watch1/app/WatchDialRenderer.kt app/src/main/java/com/watch1/app/WatchClockView.kt app/src/main/java/com/watch1/app/WatchWallpaperService.kt app/src/main/java/com/watch1/app/WatchAppWidgetProvider.kt
rtk git commit -m "feat: render Moon Phase, Golden/Blue hour arcs, Solar Noon pip, and Red Night Mode"
```

---

### Task 6: Settings UI Integration in `activity_main.xml` & `MainActivity.kt`

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/java/com/watch1/app/MainActivity.kt`

- [ ] **Step 1: Add switches to `activity_main.xml`**
In the Parameters card, add:
- `switchShowMoonPhase`
- `switchShowGoldenHour`
- `switchShowSolarNoon`
- `switchRedNightMode`
with standard dividers and padding.

- [ ] **Step 2: Bind switches in `MainActivity.kt`**
Bind each switch to its getter/setter in `LocationHelper` and invoke `clockView.refreshSettings()`.

- [ ] **Step 3: Run unit tests**
Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 4: Commit Task 6**
```bash
rtk git add app/src/main/res/layout/activity_main.xml app/src/main/java/com/watch1/app/MainActivity.kt
rtk git commit -m "feat: add Moon Phase, Golden Hour, Solar Noon, and Red Night Mode switches to settings"
```

---

### Task 7: Full Verification & Test Suite

**Files:**
- Modify: `app/src/test/java/com/watch1/app/WatchDialRendererTest.kt`

- [ ] **Step 1: Add integration unit tests for new complications and renderer modes**
- [ ] **Step 2: Run full `./gradlew test` suite**
Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew test`
Expected: PASS (all unit tests pass for both debug and release).
- [ ] **Step 3: Commit Task 7**
```bash
rtk git add app/src/test/java/com/watch1/app/WatchDialRendererTest.kt
rtk git commit -m "test: add comprehensive test suite for astronomical complications"
```
