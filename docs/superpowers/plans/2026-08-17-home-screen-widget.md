# 1watch Android Home Screen Widget Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement an adaptive, power-efficient Android Home Screen Widget (2x2 to 4x4) for **1watch** that renders the 24-hour dial, solar day/night division, 12-tier UV arc, and complications with global settings synchronization.

**Architecture:** Create `Uno24AppWidgetProvider` extending `AppWidgetProvider`. Re-use `Uno24DialRenderer` to render high-DPI bitmaps for `RemoteViews` on minute ticks (`ACTION_TIME_TICK`) and on settings/UV updates.

**Tech Stack:** Kotlin, Android AppWidgetProvider, RemoteViews, Canvas 2D Bitmap rendering, SharedPreferences.

## Global Constraints
- Transparent widget canvas so dial floats naturally on any wallpaper.
- Automatic minute updates when screen is on; 0% battery drain when screen is off.
- Tap on widget launches `MainActivity`.
- Instant synchronization when settings or UV data change.
- Always prefix shell commands with `rtk`.

---

### Task 1: Widget Layout, XML Metadata & Localization

**Files:**
- Create: `app/src/main/res/xml/uno24_appwidget_info.xml`
- Create: `app/src/main/res/layout/widget_uno24_layout.xml`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/res/values/strings.xml` and localization files

- [ ] **Step 1: Create `widget_uno24_layout.xml` with ImageView and click container**
- [ ] **Step 2: Create `uno24_appwidget_info.xml` provider definition**
- [ ] **Step 3: Register receiver in `AndroidManifest.xml`**
- [ ] **Step 4: Add widget description strings to resources**
- [ ] **Step 5: Commit**

---

### Task 2: Implement `Uno24AppWidgetProvider` & Bitmap Rendering Engine

**Files:**
- Create: `app/src/main/java/com/uno24/wallpaper/Uno24AppWidgetProvider.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/LocationHelper.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/MainActivity.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/UvRepository.kt`

- [ ] **Step 1: Implement `Uno24AppWidgetProvider` with `renderWidgetBitmap` and `updateAllWidgets`**
- [ ] **Step 2: Integrate `Uno24AppWidgetProvider.updateAllWidgets(context)` into `LocationHelper`, `MainActivity`, and `UvRepository`**
- [ ] **Step 3: Write unit tests in `Uno24AppWidgetTest.kt`**
- [ ] **Step 4: Run unit tests and assembleDebug**
- [ ] **Step 5: Commit**

---

### Task 3: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [ ] **Step 1: Install APK on Android emulator**
- [ ] **Step 2: Add 1watch Widget to home screen and capture verification screenshot**
- [ ] **Step 3: Test widget resizing (2x2, 3x3, 4x4) and theme sync**
