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

- [ ] **Step 1: Write failing unit test for `getUvColor`**

In `app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt`:
```kotlin
@Test
fun testUvColorScale() {
    assertEquals(Color.TRANSPARENT, Uno24DialRenderer.getUvColor(0.0f))
    assertEquals(Color.parseColor("#4CAF50"), Uno24DialRenderer.getUvColor(1.5f)) // Low (Green)
    assertEquals(Color.parseColor("#FDD835"), Uno24DialRenderer.getUvColor(4.2f)) // Moderate (Yellow)
    assertEquals(Color.parseColor("#FB8C00"), Uno24DialRenderer.getUvColor(6.8f)) // High (Orange)
    assertEquals(Color.parseColor("#E53935"), Uno24DialRenderer.getUvColor(9.5f)) // Very High (Red)
    assertEquals(Color.parseColor("#8E24AA"), Uno24DialRenderer.getUvColor(11.5f)) // Extreme (Violet)
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest --tests com.uno24.wallpaper.Uno24DialRendererTest.testUvColorScale`
Expected: FAIL.

- [ ] **Step 3: Implement `getUvColor` and update UV drawing in `Uno24DialRenderer.kt`**

Add companion method:
```kotlin
fun getUvColor(uvVal: Float): Int {
    return when {
        uvVal >= 11.0f -> Color.parseColor("#8E24AA") // Extreme (Violet)
        uvVal >= 8.0f -> Color.parseColor("#E53935")  // Very High (Red)
        uvVal >= 6.0f -> Color.parseColor("#FB8C00")  // High (Orange)
        uvVal >= 3.0f -> Color.parseColor("#FDD835")  // Moderate (Yellow)
        uvVal >= 0.5f -> Color.parseColor("#4CAF50")  // Low (Green)
        else -> Color.TRANSPARENT
    }
}
```

Update UV arc drawing loop:
```kotlin
        if (showUv && uvData != null && uvData.size >= 24 && !sunTimes.isPolarNight) {
            val uvArcRadius = radius * 0.94f
            uvRect.set(cx - uvArcRadius, cy - uvArcRadius, cx + uvArcRadius, cy + uvArcRadius)
            uvArcPaint.strokeWidth = radius * 0.04f

            for (h in 0 until 24) {
                val uvVal = uvData[h]
                val color = getUvColor(uvVal)
                if (color != Color.TRANSPARENT) {
                    val startAngle = timeToAngle(h.toDouble()) - 90f
                    uvArcPaint.color = color
                    canvas.drawArc(uvRect, startAngle, 15f, false, uvArcPaint)
                }
            }
        }
```

Update complication dot:
```kotlin
        if (showUvIndex) {
            val currentUv = uvData?.getOrNull(currentHour) ?: 0.0f
            val uvStr = if (currentUv > 0f) "UV %.1f".format(java.util.Locale.US, currentUv) else "UV 0"
            val uvY = cy - radius * 0.38f
            val uOffset = (complicationPaint.textSize - complicationPaint.descent() - complicationPaint.ascent()) / 2f - complicationPaint.textSize / 2f
            
            val uvColor = getUvColor(currentUv)
            if (uvColor != Color.TRANSPARENT) {
                val textW = textPaint.measureText(uvStr)
                val dotRadius = radius * 0.018f
                val dotPaint = uvArcPaint
                val dotX = cx - (textW / 2f) - (dotRadius * 3.0f)
                dotPaint.color = uvColor
                dotPaint.style = Paint.Style.FILL
                canvas.drawCircle(dotX, uvY, dotRadius, dotPaint)
                dotPaint.style = Paint.Style.STROKE
            }
            canvas.drawText(uvStr, cx, uvY + uOffset, textPaint)
        }
```

- [ ] **Step 4: Run unit tests to verify they pass**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/uno24/wallpaper/Uno24DialRenderer.kt app/src/test/java/com/uno24/wallpaper/Uno24DialRendererTest.kt
rtk git commit -m "feat: implement WHO 5-tier UV color scale for dial arc and complication dot"
```

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

- [ ] **Step 1: Replace `UNO 24` with `1watch` in all 12 strings.xml files**

- [ ] **Step 2: Run build to verify resource compilation**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" rtk ./gradlew test assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
rtk git add app/src/main/res/values*
rtk git commit -m "feat: rename application to 1watch across all 12 localizations"
```

---

### Task 3: Emulator Deployment & Visual Verification

**Files:**
- Target: `emulator-5554`

- [ ] **Step 1: Install APK on Android emulator**

Run: `rtk orca emulator install ./app/build/outputs/apk/debug/app-debug.apk --reinstall --device emulator-5554 --json`

- [ ] **Step 2: Launch `MainActivity` and capture verification screenshot**

Run: `rtk orca emulator launch com.uno24.wallpaper --activity .MainActivity --device emulator-5554 --json`

- [ ] **Step 3: Verify UV arc multi-color spectrum and complication dot on screen**
