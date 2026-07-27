# UNO 24 Android Live Wallpaper Design Specification

## Overview
An Android Live Wallpaper (`WallpaperService`) that renders a 24-hour single-hand watch dial. The clock features a single hour hand completing one full revolution every 24 hours, with a dynamic dark night zone calculated offline using the NOAA solar position formula based on the user's geolocation and date.

## Key Features
1. **UNO 24 Dial Aesthetics:**
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
   - Main activity providing instructions on how to set the live wallpaper, grant location permissions, and toggle theme modes.
