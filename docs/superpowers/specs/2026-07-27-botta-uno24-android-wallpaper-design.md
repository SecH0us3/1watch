# Botta UNO 24 Android Live Wallpaper Design Specification

## Overview
An Android Live Wallpaper (`WallpaperService`) that renders a 24-hour single-hand watch dial inspired by the Botta UNO 24 watch design. The clock features a single hour hand completing one full revolution every 24 hours, with a dynamic dark night zone calculated offline using the NOAA solar position formula based on the user's geolocation and date.

## Key Features
1. **Botta UNO 24 Dial Aesthetics:**
   - Single hour hand moving clockwise ($15^\circ$ per hour, $0.25^\circ$ per minute).
   - 24-hour circular scale: 12:00 (Noon) at the top, 00:00 (Midnight) at the bottom, 06:00 (Morning) at the left, 18:00 (Evening) at the right.
   - Hour labels and precision tick marks (1-hour primary, 10/15-minute secondary).
2. **Dynamic Sunset/Sunrise Night Arc:**
   - Shaded dark arc spanning from calculated Sunset time to Sunrise time across midnight (bottom half of dial).
   - Dynamic angle calculations updated daily based on lat/lon coordinates.
3. **Offline Astronomical Calculation (NOAA Algorithm):**
   - Pure math solar calculator (`SunTimes` engine) calculating exact sunrise/sunset times without external API calls or network dependencies.
4. **Location Handling:**
   - Android Location Manager / `FusedLocationProviderClient` for obtaining approximate device coordinates.
   - Fallback default coordinates if location permission is not granted.
5. **Battery-Efficient Live Wallpaper Engine:**
   - `WallpaperService` implementation using standard Android `Canvas` rendering.
   - Redraw triggers: time changes (minute ticks), visibility changes (screen unlock / surface created). Zero CPU/battery usage when screen is off or app is in background.
6. **Settings & Wallpaper Launcher Activity:**
   - Main activity providing instructions on how to set the live wallpaper, grant location permissions, and toggle theme modes (Dark/AMOLED vs Light theme).

## Architecture & Component Isolation
- `Uno24WallpaperService.kt`: Android `WallpaperService` lifecycle and `Engine` implementation handling canvas drawing and surface callbacks.
- `SolarCalculator.kt`: Self-contained NOAA solar algorithm implementation for computing sunrise and sunset UTC epoch timestamps given latitude, longitude, and timezone offset.
- `Uno24DialRenderer.kt`: Pure canvas rendering logic for the 24-hour dial, tick marks, numbers, night sector arc, and single hand. Decoupled from Android service lifecycle for testability.
- `LocationHelper.kt`: Encapsulates location retrieval and caching in `SharedPreferences`.
- `MainActivity.kt`: Entry point activity for opening wallpaper picker and managing permissions.

## Data Flow
1. Device unlocks / screen turns on → `Uno24WallpaperService.Engine.onVisibilityChanged(true)` is triggered.
2. `LocationHelper` retrieves cached or current location (lat, lon).
3. `SolarCalculator.getSunTimes(lat, lon, date)` calculates today's sunrise and sunset fractional hours (e.g. Sunrise 05:42, Sunset 21:15).
4. `Uno24DialRenderer` maps sunrise/sunset to angles on the 24-hour clock face ($0^\circ$ = 12:00 noon, $180^\circ$ = 00:00 midnight) and draws:
   - Dial background & night arc.
   - 24-hour ticks and numbers.
   - Hour hand pointing to current time angle.
5. Schedule next update at the start of the next minute.

## Tech Stack & Build Requirements
- Language: Kotlin
- Target SDK: Android 34 / 35 (Android 14/15)
- Min SDK: Android 26 (Android 8.0)
- Build System: Gradle with Kotlin DSL (`build.gradle.kts`)
- Dependencies: Standard AndroidX Core, Android Location services. No heavy third-party UI frameworks needed for the core wallpaper rendering to guarantee minimal APK size and zero memory overhead.
