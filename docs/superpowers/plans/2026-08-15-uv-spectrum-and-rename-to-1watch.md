# Full-Spectrum WHO UV Color Index & 1watch App Renaming Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement full-spectrum 5-tier WHO UV color coding (Green, Yellow, Orange, Red, Violet) for both the dial UV activity arc and complication indicator, and rename the application from "UNO 24" to "1watch" across all 12 localization resources.

**Architecture:** Add `getUvColor(uvVal: Float): Int` to `Uno24DialRenderer.kt` mapping UV index values to official WHO colors. Update UV activity arc rendering loop to include all daytime hours with UV $\ge 0.5$. Update `app_name` in all 12 `strings.xml` localization files.

**Tech Stack:** Kotlin, Android Canvas 2D graphics, XML Localization Resources, JUnit 4.

## Global Constraints
- WHO UV Color Bands:
  - `< 0.5`: Transparent (not drawn)
  - `0.5 .. 2.9`: Green (`#4CAF50`)
  - `3.0 .. 5.9`: Yellow (`#FDD835`)
  - `6.0 .. 7.9`: Orange (`#FB8C00`)
  - `8.0 .. 10.9`: Red (`#E53935`)
  - `11.0+`: Violet / Purple (`#8E24AA`)
- `app_name` must be set to `1watch` across all 12 XML string files.
- Always prefix shell commands with `rtk`.

---

### Task 1: WHO 5-Tier UV Color Scale in `Uno24DialRenderer`

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt`
- Test: `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`

**Interfaces:**
- Produces: `Uno24DialRenderer.getUvColor(uvVal: Float): Int`

- [x] **Step 1: Write failing unit test for `getUvColor`**
- [x] **Step 2: Run test to verify it fails**
- [x] **Step 3: Implement `getUvColor` and update UV drawing in `Uno24DialRenderer.kt`**
- [x] **Step 4: Run unit tests to verify they pass**
- [x] **Step 5: Commit**

---

### Task 2: Rename App to `1watch` Across All Localizations

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-ru/strings.xml`
- Modify: `app/src/main/res/values-es/strings.xml`
- Modify: `app/src/main/res/values-de/strings.xml`
- Modify: `app/src/main/res/values-fr/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`
- Modify: `app/src/main/res/values-ja/strings.xml`
- Modify: `app/src/main/res/values-hi/strings.xml`
- Modify: `app/src/main/res/values-la/strings.xml`
- Modify: `app/src/main/res/values-el/strings.xml`
- Modify: `app/src/main/res/values-b+ang/strings.xml`

- [x] **Step 1: Replace `UNO 24` with `1watch` in all 12 strings.xml files**
- [x] **Step 2: Run build to verify resource compilation**
- [x] **Step 3: Commit**

---

### Task 3: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [x] **Step 1: Install APK on Android emulator**
- [x] **Step 2: Launch `MainActivity` and capture verification screenshot**
- [x] **Step 3: Verify UV arc multi-color spectrum and complication dot on screen**
