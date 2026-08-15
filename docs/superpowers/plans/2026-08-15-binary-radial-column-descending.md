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

- [ ] **Step 1: Update `drawTicksAndNumerals` in `Uno24DialRenderer.kt`**

When `numeralStyle == NumeralStyle.BINARY`:
```kotlin
if (numeralStyle == NumeralStyle.BINARY) {
    val rStart = radius - tickLength - textPaint.textSize * 0.7f
    val stepR = if (numeralDisplayMode == NumeralDisplayMode.ALL) radius * 0.038f else radius * 0.045f
    val tOffset = textVerticalOffset
    for (i in labelText.indices) {
        val charRadius = rStart - i * stepR
        val cxChar = (cx + charRadius * cos(rad)).toFloat()
        val cyChar = (cy + charRadius * sin(rad)).toFloat()
        canvas.drawText(labelText[i].toString(), cxChar, cyChar + tOffset, textPaint)
    }
} else if (isRadial) {
    canvas.save()
    canvas.rotate(angle + 90f, labelX, labelY)
    canvas.drawText(labelText, labelX, labelY + textVerticalOffset, textPaint)
    canvas.restore()
} else {
    canvas.drawText(labelText, labelX, labelY + textVerticalOffset, textPaint)
}
```

- [ ] **Step 2: Run all unit tests**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew test`
Expected: PASS.

- [ ] **Step 3: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt
rtk git commit -m "feat: render binary numerals as radial columns descending to center with upright glyphs"
```

---

### Task 2: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [ ] **Step 1: Build and reinstall debug APK on emulator**

Run: `rtk orca emulator install ./app/build/outputs/apk/debug/app-debug.apk --reinstall --device emulator-5554 --json`

- [ ] **Step 2: Launch `MainActivity` and capture screenshot with Binary numerals**

- [ ] **Step 3: Visually verify upright glyphs descending radially to center**
