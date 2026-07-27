# UV Activity Arc Feature Design Specification

## Overview
Adds UV Index activity integration to the UNO 24 Android Live Wallpaper. Hourly UV index forecast data is fetched asynchronously from the free Open-Meteo API (`api.open-meteo.com`). High UV hours ($\text{UV} \ge 3.0$) are rendered as a subtle color-coded arc on the daytime sector of the 24-hour dial. The feature can be toggled on/off in the app settings.

## Features
1. **Open-Meteo API Fetcher (`UvRepository.kt`):**
   - Asynchronous HTTP request (using Kotlin `HttpURLConnection` / background thread) to:
     `https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&hourly=uv_index&timezone=auto`
   - Parses 24-hour UV array for today into `FloatArray(24)`.
   - Caches forecast in `SharedPreferences` to limit network requests to once every 3-6 hours.
2. **Offline Fallback Estimation:**
   - If offline or network fails, calculates estimated UV index from solar elevation angle: $\text{UV} \approx \max(0, 12 \cdot \sin(\text{SolarElevation}))$.
3. **Canvas UV Arc Rendering (`Uno24DialRenderer.kt`):**
   - Highlights daytime hours where UV index is active ($\text{UV} \ge 3.0$).
   - Color scale:
     - Moderate ($\text{UV } 3-5$): Yellow `#FFD600`
     - High ($\text{UV } 6-7$): Orange `#FF6D00`
     - Very High / Extreme ($\text{UV } \ge 8$): Purple `#D500F9`
   - Rendered as an inner/outer arc stroke along the daytime sector hours.
4. **Settings Toggle (`MainActivity.kt` & `LocationHelper.kt`):**
   - Switch in `activity_main.xml`: "Show UV Activity Arc".
   - State saved in `Uno24Prefs` (`KEY_SHOW_UV`).

## Component Architecture
- `UvRepository.kt`: Handles background API fetching, JSON parsing, offline mathematical fallback, and caching.
- `LocationHelper.kt`: Updated with `getShowUv(context)` and `saveShowUv(context, enabled)`.
- `Uno24DialRenderer.kt`: Draws the UV arc on the daytime clock sector when `showUv` is true and `uvData` is present.
- `Uno24WallpaperService.kt`: Periodically triggers `UvRepository` background sync when visible.
- `MainActivity.kt`: Switch UI element for toggling UV arc display.
