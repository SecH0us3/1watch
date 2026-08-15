# Binary Radial Column Descending Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Option B for binary numeral rendering in `Uno24DialRenderer.kt` where the 5 bits of each hour number form a radial column descending from the hour tick to the dial center with upright glyphs.

**Architecture:** Update `drawTicksAndNumerals` in `Uno24DialRenderer.kt` to detect when `numeralStyle == NumeralStyle.BINARY` and render each character of `labelText` at descending radial distances $r_i = r_{\text{start}} - i \cdot \Delta r$ along the hour angle spoke.

**Tech Stack:** Kotlin, Android Canvas 2D graphics, Android Emulator.

## Global Constraints
- Binary numeral strings are 5-bit (`00000`..`10111`).
- Bits must descend from outer tick ($r_{\text{start}}$) to center with upright orientation.
- Dual-mask contrast (Day/Night zones) must be preserved.
- Always prefix shell commands with `rtk`.

---

### Task 1: Implement Binary Radial Column in `Uno24DialRenderer.kt`

**Files:**
- Modify: `app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt`

- [x] **Step 1: Update `drawTicksAndNumerals` in `Uno24DialRenderer.kt`**
- [x] **Step 2: Run all unit tests**
- [x] **Step 3: Commit**

---

### Task 2: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [x] **Step 1: Build and reinstall debug APK on emulator**
- [x] **Step 2: Launch `MainActivity` and capture screenshot with Binary numerals**
- [x] **Step 3: Visually verify upright glyphs descending radially to center**
