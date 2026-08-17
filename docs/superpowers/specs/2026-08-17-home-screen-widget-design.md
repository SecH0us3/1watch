# 1watch Android Home Screen Widget Design

## 1. Overview
This feature introduces an interactive, scalable Android Home Screen Widget for **1watch**. The widget renders the complete 24-hour single-hand dial with dual-mask day/night solar horizon division, the 12-tier WHO UV spectrum arc, complications (date & UV index), and custom numeral systems (Arabic, Roman, Binary radial, Hebrew, Greek, Kanji, etc.).

---

## 2. Architecture & Components

```
+-----------------------------------------------------------+
|                      1watch Widget                        |
+-----------------------------------------------------------+
                             |
         +-------------------+-------------------+
         |                                       |
         v                                       v
[1watchAppWidgetProvider]              [WidgetUpdateReceiver]
(AppWidgetProvider lifecycle)          (ACTION_TIME_TICK / SCREEN_ON)
         |                                       |
         +-------------------+-------------------+
                             |
                             v
               [Uno24DialRenderer.draw()]
           (Generates high-res Bitmap for RemoteViews)
                             |
                             v
                    [AppWidgetManager]
               (Updates Home Screen Widgets)
```

### Components:
1. **`Uno24AppWidgetProvider.kt`**:
   - Extends `AppWidgetProvider`.
   - Handles `onUpdate`, `onAppWidgetOptionsChanged` (responsive scaling for 2x2 to 4x4), `onEnabled`, `onDisabled`.
   - Binds `PendingIntent` to launch `MainActivity` upon click.
2. **`WidgetUpdateService` / `WidgetTickReceiver`**:
   - Listens to `ACTION_TIME_TICK`, `ACTION_SCREEN_ON`, and `ACTION_USER_PRESENT` while device is active to push minute updates to widgets.
   - Zero background wakeups or battery drain when screen is off.
3. **`Uno24AppWidgetRenderer`**:
   - Reuses `Uno24DialRenderer` to render the clock face onto an in-memory ARGB_8888 `Bitmap`.
   - Calculates sun times using `SolarCalculator` with saved GPS coordinates.
   - Renders 12-tier UV activity arc and complication values using `UvRepository`.
4. **App Widget XML Metadata (`appwidget_provider_info.xml`)**:
   - `minWidth="110dp"`, `minHeight="110dp"` (2x2 grid cell minimum).
   - `targetCellWidth="3"`, `targetCellHeight="3"`.
   - `resizeMode="horizontal|vertical"`.
   - `widgetCategory="home_screen"`.
   - `previewImage` / `previewLayout`.

---

## 3. Visual & Interaction Design

### Dynamic Sizing & Layout:
- Scales dynamically from small (2x2 cells, ~110x110 dp) to full size (4x4 cells, ~300x300 dp).
- Dial center is positioned at $(W/2, H/2)$ with transparent canvas background so the dial floats naturally over any desktop wallpaper.

### Settings & Theme Synchronization:
- Automatically reads the shared `ClockConfig` from `LocationHelper`.
- Whenever any setting (Theme, Numeral Style, Font, Orientation, Complications) changes in `MainActivity`, `Uno24AppWidgetProvider.updateAllWidgets(context)` is invoked for instant home screen update.
- Whenever `UvRepository` finishes fetching new Open-Meteo UV data or GPS changes, widgets update immediately.

### Tap Action:
- Tapping anywhere on the widget launches `MainActivity` with transition animation.

---

## 4. Power & Resource Constraints
- Minute updates advance the 24-hour hand by $0.25^\circ$ with negligible CPU overhead (<2ms per Bitmap draw).
- Screen-off state unregisters the tick receiver, achieving 0% idle battery consumption.

---

## 5. Verification & Testing
- Unit tests for widget sizing calculations and Bitmap rendering helper.
- Emulator deployment and interactive testing on `emulator-5554` (adding widget to home screen, resizing, theme switching, tap behavior).
