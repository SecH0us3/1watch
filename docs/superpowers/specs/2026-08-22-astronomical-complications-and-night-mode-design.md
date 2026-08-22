# Astronomical Complications & Red Cockpit Night Mode Design Specification

## Overview
This specification details four lightweight, purely mathematical and vector-drawn features for **1watch**:
1. **Moon Phase Indicator (Фаза Луны)**: Analytical synodic moon phase calculation and vector moon disc complication in the lower dial sector.
2. **Golden & Blue Hour Arcs («Золотой» и «Синий» час)**: Thin color-coded micro-arcs along the dial rim highlighting photographer's golden hour ($0^\circ \to +6^\circ$) and blue hour / civil twilight ($-6^\circ \to 0^\circ$).
3. **Solar Noon Marker (Истинный солнечный полдень)**: Solar noon culmination calculation with an accent golden solar glyph/point on the 24-hour rim.
4. **Red Cockpit Night Mode (Красный ночной режим)**: Automatic monochrome aviation red/crimson night theme active between astronomical sunset and sunrise to preserve human dark adaptation.

Zero external dependencies or image assets are added, maintaining minimal binary size.

---

## 1. Mathematical Models & Astronomical Calculations

### 1.1 Moon Phase Calculation (`MoonCalculator.kt`)
- **Synodic Month Period**: $T_{synodic} = 29.530588853$ days.
- **Reference Epoch**: Known New Moon on Jan 11, 2024 at 11:57 UTC (Julian Day number $2460321.0$).
- **Normalized Phase**: $P \in [0.0, 1.0)$, where:
  - $P \approx 0.0$: New Moon
  - $0.0 < P < 0.25$: Waxing Crescent
  - $P \approx 0.25$: First Quarter
  - $0.25 < P < 0.5$: Waxing Gibbous
  - $P \approx 0.5$: Full Moon
  - $0.5 < P < 0.75$: Waning Gibbous
  - $P \approx 0.75$: Last Quarter
  - $0.75 < P < 1.0$: Waning Crescent
- **Illumination Fraction**: $k = \frac{1 - \cos(2\pi P)}{2} \in [0.0, 1.0]$.
- **Data Model**:
  ```kotlin
  data class MoonInfo(
      val phase: Double, // 0.0..1.0
      val illumination: Double, // 0.0..1.0
      val phaseName: MoonPhaseName
  )
  enum class MoonPhaseName {
      NEW_MOON, WAXING_CRESCENT, FIRST_QUARTER, WAXING_GIBBOUS,
      FULL_MOON, WANING_GIBBOUS, LAST_QUARTER, WANING_CRESCENT
  }
  ```

### 1.2 Golden & Blue Hour Calculations (`SolarCalculator.kt`)
Extended `SolarCalculator` calculations using zenith angles:
- **Civil Twilight / Blue Hour Boundary**: $\text{zenith} = 96.0^\circ$ (sun at $-6^\circ$ elevation).
- **Official Sunrise/Sunset**: $\text{zenith} = 90.833^\circ$ (sun at $-0.833^\circ$ elevation).
- **Golden Hour Boundary**: $\text{zenith} = 84.0^\circ$ (sun at $+6^\circ$ elevation).
- Extended data model:
  ```kotlin
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
  ```

### 1.3 Solar Noon Calculation (`SolarCalculator.kt`)
- **Formula**:
  $$\text{solarNoonMinutes} = 720.0 - 4.0 \cdot \lambda - \text{eqTime} + (\text{tzOffset} \cdot 60.0)$$
  $$\text{solarNoonHour} = \left(\frac{\text{solarNoonMinutes}}{60.0}\right) \pmod{24.0}$$

---

## 2. Rendering & Complications (`WatchDialRenderer.kt`)

### 2.1 Moon Phase Complication
- Positioned in the lower quadrant (above the date badge, centered at $(cx, cy + r \cdot 0.48)$).
- **Vector Moon Disc Drawing**:
  - Diameter $\approx 16\text{ dp}$.
  - Dark lunar base disk (`#2A2D3A` or dark theme color).
  - Illuminated white/silver crescent or gibbous shape drawn using clip paths or double arc curves based on phase $P$.
  - Moon phase percentage text (`"78%"`) rendered subtly underneath in small font ($7\text{ sp}$).

### 2.2 Golden & Blue Hour Rim Arcs
- Rendered on the dial circumference along the hour track (radius $r \cdot 0.88$, stroke width $2.5\text{ dp}$):
  - **Morning Blue Hour Arc**: From `morningBlueHourStart` to `sunriseHour`, colored `#0288D1` (80% alpha).
  - **Morning Golden Hour Arc**: From `sunriseHour` to `morningGoldenHourEnd`, colored `#FFB300` (85% alpha).
  - **Evening Golden Hour Arc**: From `eveningGoldenHourStart` to `sunsetHour`, colored `#FFB300` (85% alpha).
  - **Evening Blue Hour Arc**: From `sunsetHour` to `eveningBlueHourEnd`, colored `#0288D1` (80% alpha).

### 2.3 Solar Noon Marker
- Rendered at angle $\theta_{\text{noon}} = \text{timeToAngle}(\text{solarNoonHour}) - 90^\circ$:
  - A glowing golden pip / dot ($2.5\text{ dp}$) at radius $r \cdot 0.84$.
  - Radial micro-tick with subtle sun symbol ☀️.

### 2.4 Red Cockpit Night Mode
- When `config.redNightMode == true` and the current time is during nighttime (`!isDaytime`):
  - **Theme Override in Renderer**:
    - Day/Night background: deep black/dark crimson (`#060101` / `#160303`).
    - Numerals and dial ticks: Cockpit red (`#EF5350` / `#D32F2F`).
    - Hand: High-visibility aviation luminous red (`#FF1744`).
    - UV arc and badges: Tone-mapped to red/amber intensities to avoid blue/green light leakage in dark environments.

---

## 3. Configuration & State Management (`LocationHelper.kt`)

### 3.1 `ClockConfig` Extensions
```kotlin
data class ClockConfig(
    ...
    val showMoonPhase: Boolean = true,
    val showGoldenHour: Boolean = false,
    val showSolarNoon: Boolean = false,
    val redNightMode: Boolean = false
)
```

### 3.2 `SharedPreferences` Keys
- `key_show_moon_phase` (default: `true`)
- `key_show_golden_hour` (default: `false`)
- `key_show_solar_noon` (default: `false`)
- `key_red_night_mode` (default: `false`)

---

## 4. UI & Localization

### 4.1 Settings Switches (`activity_main.xml` under Parameters card)
- `switchShowMoonPhase`: Moon Phase / Фаза Луны
- `switchShowGoldenHour`: Golden & Blue Hours / «Золотой» и «Синий» час
- `switchShowSolarNoon`: True Solar Noon Marker / Метка истинного полудня
- `switchRedNightMode`: Red Night Mode (Cockpit) / Красный ночной режим

### 4.2 Translations
All 4 strings translated across all 11 locales:
- `values/strings.xml` (EN)
- `values-ru/strings.xml` (RU)
- `values-de/strings.xml` (DE)
- `values-fr/strings.xml` (FR)
- `values-es/strings.xml` (ES)
- `values-zh-rCN/strings.xml` (ZH)
- `values-ja/strings.xml` (JA)
- `values-hi/strings.xml` (HI)
- `values-el/strings.xml` (EL)
- `values-la/strings.xml` (LA)
- `values-b+ang/strings.xml` (ANG)

---

## 5. Testing & Verification Plan
- **Unit Tests in `SolarCalculatorTest.kt` & `MoonCalculatorTest.kt`**:
  - Known new moon, full moon, and quarter dates validation.
  - Solar noon symmetry: `solarNoonHour` matches local meridian time accurately.
  - Golden/Blue hour angle ranges for equinoxes, solstices, and high latitudes.
  - Polar day / polar night handling for twilight angles.
- **Unit Tests in `WatchDialRendererTest.kt`**:
  - `ClockConfig` default values and serialization.
  - Red night mode activation logic when day vs night.
  - Non-crashing canvas draws with all combinations of toggles enabled/disabled.
- **Emulator Verification**:
  - Visual verification on Android emulator for Moon Phase, Golden/Blue hour arcs, Solar Noon pip, and Red Night Mode.
