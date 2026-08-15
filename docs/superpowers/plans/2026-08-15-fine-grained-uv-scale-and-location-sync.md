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

- [x] **Step 1: Write failing test in `Uno24DialRendererTest.kt`**
- [x] **Step 2: Run test to verify it fails**
- [x] **Step 3: Update `getUvColor` in `Uno24DialRenderer.kt`**
- [x] **Step 4: Run unit tests to verify they pass**
- [x] **Step 5: Commit**

---

### Task 2: Active Location Sync & Coordinate-Keyed UV Cache

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/LocationHelper.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/UvRepository.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24ClockView.kt`
- Modify: `app/src/main/java/com/uno24/wallpaper/MainActivity.kt`

- [x] **Step 1: Enhance `UvRepository.kt` with coordinate/date cache keying and callback**
- [x] **Step 2: Enhance `LocationHelper.kt` to request active location updates and notify listener**
- [x] **Step 3: Update `Uno24ClockView.kt` and `MainActivity.kt` to register for location and UV updates**
- [x] **Step 4: Run unit tests and assembleDebug**
- [x] **Step 5: Commit**

---

### Task 3: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [x] **Step 1: Install APK on emulator**
- [x] **Step 2: Launch `MainActivity` and verify UV index and 12-tier color arc**
