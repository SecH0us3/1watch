# Design Specification: Day/Night Smooth Gradient Transition

## Overview
Add a user-configurable toggle to the 1watch Android application that renders a subtle, smooth twilight gradient transition between daytime and nighttime sectors on the 24-hour watch dial, replacing the sharp sector cutoffs when enabled.

## Requirements
1. **Settings Switch**:
   - Add a switch toggle in the "Parameters" card of the settings UI (`activity_main.xml`).
   - Title: "Day & night smooth gradient" / "Градиентный переход дня и ночи".
   - Persist state in `SharedPreferences` via `LocationHelper`.
   - Default state: `false` (sharp transition, preserving existing default visual appearance).

2. **Visual Rendering (`WatchDialRenderer`)**:
   - Support `gradientDayNight: Boolean` parameter in `WatchDialRenderer.draw()`.
   - When `gradientDayNight == false`: render sharp day/night sectors via `arcTo` / `clipPath` as currently implemented.
   - When `gradientDayNight == true`:
     - Handle polar day and polar night gracefully (solid daytime / nighttime fill).
     - For standard sunrise/sunset solar cycles: compute a `SweepGradient` around the center coordinates `(cx, cy)` spanning a ~1.5 hour twilight window (±11.25° around sunset and sunrise angles).
     - Blend smoothly from `dayZoneColor` to `nightZoneColor` during dusk and from `nightZoneColor` to `dayZoneColor` during dawn.
     - Paint the dial base circle with this gradient.
     - Maintain dual-contrast split text and tick rendering for crisp legibility.

3. **System-wide Propagation**:
   - Propagate the setting to `WatchClockView` (main activity preview), `WatchWallpaperService` (live wallpaper), and `WatchAppWidgetProvider` (home screen widgets).

4. **Multi-language Support**:
   - Provide localized string entries for all supported languages: English (`values`), Russian (`values-ru`), Spanish (`values-es`), German (`values-de`), French (`values-fr`), Chinese (`values-zh-rCN`), Japanese (`values-ja`), Hindi (`values-hi`), Ancient Greek (`values-el`), Latin (`values-la`), Old English (`values-b+ang`).

## Technical Architecture

### 1. Data Model & Preferences
- In `LocationHelper.kt`:
  - `KEY_GRADIENT_DAY_NIGHT = "key_gradient_day_night"`
  - `fun getGradientDayNight(context: Context): Boolean` (default `false`)
  - `fun saveGradientDayNight(context: Context, enabled: Boolean)`
  - Update `ClockConfig` data class:
    ```kotlin
    data class ClockConfig(
        ...
        val gradientDayNight: Boolean = false
    )
    ```
  - Update `LocationHelper.loadConfig(context: Context)` to load `gradientDayNight`.

### 2. Gradient Calculation & Rendering Details
- In `WatchDialRenderer.kt`:
  - Helper function or internal block to compute `SweepGradient` color stops.
  - Angular geometry:
    - 24 hours corresponds to 360° (15° per hour). 12:00 is straight up (-90° in standard canvas coordinates).
    - Sunset angle: $\theta_{\text{sunset}} = \text{timeToAngle}(\text{sunsetHour}) - 90^\circ$.
    - Sunrise angle: $\theta_{\text{sunrise}} = \text{timeToAngle}(\text{sunriseHour}) - 90^\circ$.
    - Twilight transition half-width: $\delta = 11.25^\circ$ (~0.75 hours / 45 minutes on either side, total 1.5 hours twilight window).
    - Angular positions are mapped into normalized $[0.0, 1.0]$ stops around the circle with appropriate color transitions:
      - $\theta_{\text{sunset}} - \delta \rightarrow \text{dayZoneColor}$
      - $\theta_{\text{sunset}} + \delta \rightarrow \text{nightZoneColor}$
      - $\theta_{\text{sunrise}} - \delta \rightarrow \text{nightZoneColor}$
      - $\theta_{\text{sunrise}} + \delta \rightarrow \text{dayZoneColor}$
    - The `SweepGradient` is applied to a dedicated `gradientPaint` (or `dayZonePaint`) and drawn over the dial circle `dialRect`.

### 3. Settings UI & Localization
- In `res/layout/activity_main.xml`:
  - Add `SwitchMaterial` with ID `@+id/switchGradientDayNight` in the Parameters card.
- In `MainActivity.kt`:
  - Initialize `switchGradientDayNight.isChecked = LocationHelper.getGradientDayNight(this)`.
  - Add change listener to save preference and call `clockView.refreshSettings()`.

### 4. Verification & Testing
- Unit tests in `WatchDialRendererTest.kt`:
  - Test default config values and preferences helper methods.
  - Verify gradient angle computation and polar day/night handling without exceptions.
- End-to-end rendering check via app build / test suite.
