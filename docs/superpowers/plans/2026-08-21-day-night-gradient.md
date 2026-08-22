# Day/Night Smooth Gradient Transition Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a user-configurable toggle to 1watch that renders a smooth twilight gradient transition between day and night sectors on the 24-hour watch dial.

**Architecture:** Extend `ClockConfig` and `LocationHelper` with a `gradientDayNight` boolean preference; update `WatchDialRenderer` to generate an angular `SweepGradient` with twilight transition stops around sunrise and sunset angles; propagate the configuration through `WatchClockView`, `WatchWallpaperService`, and `WatchAppWidgetProvider`; add the toggle to `MainActivity` and `activity_main.xml` with full localization across all 11 supported languages.

**Tech Stack:** Kotlin, Android Canvas 2D / `SweepGradient`, Material Components `SwitchMaterial`, Android SharedPreferences, JUnit 4.

## Global Constraints
- Prefix shell commands with `rtk` (e.g., `rtk git`, `rtk ./gradlew`).
- Test runner command: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew test`.
- Preserve existing default appearance (`gradientDayNight == false` remains default).
- Support all 11 app localization locales.

---

### Task 1: Add Localized String Resources

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-ru/strings.xml`
- Modify: `app/src/main/res/values-es/strings.xml`
- Modify: `app/src/main/res/values-de/strings.xml`
- Modify: `app/src/main/res/values-fr/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`
- Modify: `app/src/main/res/values-ja/strings.xml`
- Modify: `app/src/main/res/values-hi/strings.xml`
- Modify: `app/src/main/res/values-el/strings.xml`
- Modify: `app/src/main/res/values-la/strings.xml`
- Modify: `app/src/main/res/values-b+ang/strings.xml`

**Interfaces:**
- Produces: `@string/switch_gradient_day_night` in all 11 locales.

- [ ] **Step 1: Add strings to `values/strings.xml` and all 10 localized files**

In `app/src/main/res/values/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Day &amp; night smooth gradient</string>
```

In `app/src/main/res/values-ru/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Градиентный переход дня и ночи</string>
```

In `app/src/main/res/values-es/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Gradiente suave de día y noche</string>
```

In `app/src/main/res/values-de/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Sanfter Tag- &amp; Nacht-Farbverlauf</string>
```

In `app/src/main/res/values-fr/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Dégradé fluide jour &amp; nuit</string>
```

In `app/src/main/res/values-zh-rCN/strings.xml`:
```xml
    <string name="switch_gradient_day_night">昼夜平滑渐变</string>
```

In `app/src/main/res/values-ja/strings.xml`:
```xml
    <string name="switch_gradient_day_night">昼夜のスムーズグラデーション</string>
```

In `app/src/main/res/values-hi/strings.xml`:
```xml
    <string name="switch_gradient_day_night">दिन और रात का स्मूथ ग्रेडिएंट</string>
```

In `app/src/main/res/values-el/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Ομαλή διαβάθμιση ημέρας και νύχτας</string>
```

In `app/src/main/res/values-la/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Gradatio levis diei et noctis</string>
```

In `app/src/main/res/values-b+ang/strings.xml`:
```xml
    <string name="switch_gradient_day_night">Dæges and nihte smēðe scirung</string>
```

- [ ] **Step 2: Commit**

```bash
rtk git add app/src/main/res/values*/strings.xml
rtk git commit -m "feat: add localized strings for day-night gradient switch"
```

---

### Task 2: Update Data Model and Preferences in `LocationHelper.kt`

**Files:**
- Modify: `app/src/main/java/com/watch1/app/LocationHelper.kt`
- Modify: `app/src/test/java/com/watch1/app/WatchDialRendererTest.kt`

**Interfaces:**
- Produces: `ClockConfig.gradientDayNight: Boolean`
- Produces: `LocationHelper.KEY_GRADIENT_DAY_NIGHT: String`
- Produces: `LocationHelper.getGradientDayNight(context: Context): Boolean`
- Produces: `LocationHelper.saveGradientDayNight(context: Context, enabled: Boolean)`

- [ ] **Step 1: Write test for `gradientDayNight` default in `WatchDialRendererTest.kt`**

```kotlin
    @Test
    fun testClockConfigDefaultGradientDayNight() {
        val config = ClockConfig(
            lat = 55.75,
            lon = 37.61,
            theme = DialTheme.CLASSIC_DARK,
            showUv = true,
            numeralStyle = NumeralStyle.ARABIC,
            numeralOrientation = NumeralOrientation.UPRIGHT,
            numeralDisplayMode = NumeralDisplayMode.ALL,
            fontSizeScale = 1.0f,
            numeralFont = NumeralFont.SANS_SERIF,
            handStyle = HandStyle.BOTTA_NEEDLE,
            bgMode = BackgroundMode.THEME_DEFAULT,
            customColor = 0
        )
        assertEquals(false, config.gradientDayNight)
    }
```

- [ ] **Step 2: Run test to verify failure before implementation**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests "com.watch1.app.WatchDialRendererTest.testClockConfigDefaultGradientDayNight"`
Expected: FAIL (unresolved reference `gradientDayNight`)

- [ ] **Step 3: Update `ClockConfig` and `LocationHelper.kt`**

In `app/src/main/java/com/watch1/app/LocationHelper.kt`:
Add `val gradientDayNight: Boolean = false` to `ClockConfig`:
```kotlin
data class ClockConfig(
    val lat: Double,
    val lon: Double,
    val theme: DialTheme,
    val showUv: Boolean,
    val showDate: Boolean = true,
    val showUvIndex: Boolean = true,
    val numeralStyle: NumeralStyle,
    val numeralOrientation: NumeralOrientation,
    val numeralDisplayMode: NumeralDisplayMode,
    val fontSizeScale: Float,
    val numeralFont: NumeralFont,
    val handStyle: HandStyle,
    val bgMode: BackgroundMode,
    val customColor: Int,
    val bezelStyle: BezelStyle = BezelStyle.NONE,
    val showBrandLogo: Boolean = true,
    val gradientDayNight: Boolean = false
)
```

In `LocationHelper` object:
```kotlin
    private const val KEY_GRADIENT_DAY_NIGHT = "key_gradient_day_night"

    fun getGradientDayNight(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_GRADIENT_DAY_NIGHT, false)
    }

    fun saveGradientDayNight(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_GRADIENT_DAY_NIGHT, enabled).apply()
    }
```

Update `loadConfig(context)` to populate `gradientDayNight`:
```kotlin
        return ClockConfig(
            lat = lat,
            lon = lon,
            theme = getSavedTheme(context),
            showUv = getShowUv(context),
            showDate = getShowDate(context),
            showUvIndex = getShowUvIndex(context),
            numeralStyle = getNumeralStyle(context),
            numeralOrientation = getNumeralOrientation(context),
            numeralDisplayMode = getNumeralDisplayMode(context),
            fontSizeScale = getFontSizeScale(context),
            numeralFont = getNumeralFont(context),
            handStyle = getHandStyle(context),
            bgMode = getBackgroundMode(context),
            customColor = getCustomColor(context),
            bezelStyle = getBezelStyle(context),
            showBrandLogo = getShowBrandLogo(context),
            gradientDayNight = getGradientDayNight(context)
        )
```

- [ ] **Step 4: Run test to verify it passes**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests "com.watch1.app.WatchDialRendererTest.testClockConfigDefaultGradientDayNight"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/watch1/app/LocationHelper.kt app/src/test/java/com/watch1/app/WatchDialRendererTest.kt
rtk git commit -m "feat: add gradientDayNight preference and ClockConfig field"
```

---

### Task 3: Implement Gradient Day/Night Rendering in `WatchDialRenderer.kt`

**Files:**
- Modify: `app/src/main/java/com/watch1/app/WatchDialRenderer.kt`
- Modify: `app/src/test/java/com/watch1/app/WatchDialRendererTest.kt`

**Interfaces:**
- Consumes: `sunTimes: SunTimes`, `theme: DialTheme`, `gradientDayNight: Boolean`
- Produces: `fun createDayNightGradient(cx: Float, cy: Float, sunsetHour: Double, sunriseHour: Double, dayColor: Int, nightColor: Int): SweepGradient`

- [ ] **Step 1: Write unit test for gradient color stops and angles math**

In `app/src/test/java/com/watch1/app/WatchDialRendererTest.kt`:
```kotlin
    @Test
    fun testGradientAngleCalculation() {
        // Sunset at 18:00 (90 deg canvas angle) and sunrise at 06:00 (270 deg canvas angle)
        val stops = WatchDialRenderer.calculateGradientStops(
            sunsetHour = 18.0,
            sunriseHour = 6.0,
            dayColor = 0xFFFFFFFF.toInt(),
            nightColor = 0xFF000000.toInt()
        )
        // Check that stops are sorted monotonically in [0f..1f]
        for (i in 0 until stops.positions.size - 1) {
            org.junit.Assert.assertTrue(stops.positions[i] <= stops.positions[i + 1])
        }
        assertEquals(0f, stops.positions.first(), 0.001f)
        assertEquals(1f, stops.positions.last(), 0.001f)
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests "com.watch1.app.WatchDialRendererTest.testGradientAngleCalculation"`
Expected: FAIL

- [ ] **Step 3: Implement `calculateGradientStops` and gradient rendering in `WatchDialRenderer.kt`**

In `WatchDialRenderer.kt`:
Add data class and calculation helper in `companion object`:
```kotlin
        data class GradientStops(val colors: IntArray, val positions: FloatArray)

        fun calculateGradientStops(
            sunsetHour: Double,
            sunriseHour: Double,
            dayColor: Int,
            nightColor: Int
        ): GradientStops {
            val sunsetAngle = (timeToAngle(sunsetHour) - 90f).mod(360f)
            val sunriseAngle = (timeToAngle(sunriseHour) - 90f).mod(360f)
            val delta = 11.25f // 45 min twilight transition half-width (1.5h total)

            // Create transition boundary pairs in [0..360)
            val p1 = (sunsetAngle - delta).mod(360f)  // day -> start transition to night
            val p2 = (sunsetAngle + delta).mod(360f)  // end transition -> pure night
            val p3 = (sunriseAngle - delta).mod(360f) // night -> start transition to day
            val p4 = (sunriseAngle + delta).mod(360f) // end transition -> pure day

            data class AngleColorStop(val angle: Float, val color: Int)
            val rawStops = mutableListOf(
                AngleColorStop(p1, dayColor),
                AngleColorStop(p2, nightColor),
                AngleColorStop(p3, nightColor),
                AngleColorStop(p4, dayColor)
            )
            rawStops.sortBy { it.angle }

            // Sample colors at 0 deg and 360 deg to ensure seamless wrap-around
            fun colorAt(angle: Float): Int {
                val a = angle.mod(360f)
                val isNight = if (sunsetAngle < sunriseAngle) {
                    a in (sunsetAngle + delta)..(sunriseAngle - delta)
                } else {
                    a >= (sunsetAngle + delta) || a <= (sunriseAngle - delta)
                }
                return if (isNight) nightColor else dayColor
            }

            val resultList = mutableListOf<AngleColorStop>()
            resultList.add(AngleColorStop(0f, colorAt(0f)))
            for (stop in rawStops) {
                if (stop.angle > 0f && stop.angle < 360f) {
                    resultList.add(stop)
                }
            }
            resultList.add(AngleColorStop(360f, colorAt(360f)))
            resultList.sortBy { it.angle }

            val colors = IntArray(resultList.size) { resultList[it].color }
            val positions = FloatArray(resultList.size) { (resultList[it].angle / 360f).coerceIn(0f, 1f) }

            return GradientStops(colors, positions)
        }
```

Add `gradientPaint` member:
```kotlin
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
```

Update `draw(...)` to accept `gradientDayNight: Boolean = false`:
```kotlin
    fun draw(
        canvas: Canvas,
        width: Int,
        height: Int,
        timeHourFraction: Double,
        sunTimes: SunTimes,
        theme: DialTheme = DialTheme.CLASSIC_DARK,
        showUv: Boolean = true,
        showDate: Boolean = true,
        showUvIndex: Boolean = true,
        date: LocalDate = LocalDate.now(),
        uvData: FloatArray? = null,
        numeralStyle: NumeralStyle = NumeralStyle.ARABIC,
        numeralOrientation: NumeralOrientation = NumeralOrientation.UPRIGHT,
        numeralDisplayMode: NumeralDisplayMode = NumeralDisplayMode.ALL,
        fontSizeScale: Float = 1.0f,
        numeralFont: NumeralFont = NumeralFont.SANS_SERIF,
        handStyle: HandStyle = HandStyle.BOTTA_NEEDLE,
        bgMode: BackgroundMode = BackgroundMode.THEME_DEFAULT,
        customColor: Int = Color.parseColor("#0A0F1D"),
        bgBitmap: Bitmap? = null,
        isWallpaper: Boolean = false,
        bezelStyle: BezelStyle = BezelStyle.NONE,
        showBrandLogo: Boolean = true,
        gradientDayNight: Boolean = false
    ) {
```

In section 2 of `draw(...)`:
```kotlin
        // 2. Draw Day/Night Sectors or Gradient
        dayZonePath.reset()
        nightZonePath.reset()

        if (sunTimes.isPolarNight) {
            nightZonePath.addCircle(cx, cy, radius, Path.Direction.CW)
            if (!gradientDayNight) {
                canvas.drawPath(nightZonePath, nightZonePaint)
            } else {
                canvas.drawCircle(cx, cy, radius, nightZonePaint)
            }
        } else if (sunTimes.isPolarDay) {
            dayZonePath.addCircle(cx, cy, radius, Path.Direction.CW)
            if (!gradientDayNight) {
                canvas.drawPath(dayZonePath, dayZonePaint)
            } else {
                canvas.drawCircle(cx, cy, radius, dayZonePaint)
            }
        } else {
            val sunsetAngle = timeToAngle(sunTimes.sunsetHour) - 90f
            val sunriseAngle = timeToAngle(sunTimes.sunriseHour) - 90f
            var sweepAngle = (sunriseAngle - sunsetAngle).mod(360f)
            if (sweepAngle <= 0) sweepAngle += 360f

            // Night Sector Arc Path
            nightZonePath.moveTo(cx, cy)
            nightZonePath.arcTo(dialRect, sunsetAngle, sweepAngle)
            nightZonePath.close()

            // Day Sector Arc Path
            dayZonePath.moveTo(cx, cy)
            dayZonePath.arcTo(dialRect, sunriseAngle, 360f - sweepAngle)
            dayZonePath.close()

            if (gradientDayNight) {
                val stops = calculateGradientStops(
                    sunTimes.sunsetHour,
                    sunTimes.sunriseHour,
                    theme.dayZoneColor,
                    theme.nightZoneColor
                )
                gradientPaint.shader = SweepGradient(cx, cy, stops.colors, stops.positions)
                canvas.drawCircle(cx, cy, radius, gradientPaint)
            } else {
                if (!dayZonePath.isEmpty) {
                    canvas.drawPath(dayZonePath, dayZonePaint)
                }
                if (!nightZonePath.isEmpty) {
                    canvas.drawPath(nightZonePath, nightZonePaint)
                }
            }
        }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests "com.watch1.app.WatchDialRendererTest.testGradientAngleCalculation"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/watch1/app/WatchDialRenderer.kt app/src/test/java/com/watch1/app/WatchDialRendererTest.kt
rtk git commit -m "feat: implement day/night SweepGradient rendering"
```

---

### Task 4: Propagate `gradientDayNight` to Views, Wallpaper, and Widgets

**Files:**
- Modify: `app/src/main/java/com/watch1/app/WatchClockView.kt`
- Modify: `app/src/main/java/com/watch1/app/WatchWallpaperService.kt`
- Modify: `app/src/main/java/com/watch1/app/WatchAppWidgetProvider.kt`

**Interfaces:**
- Consumes: `config.gradientDayNight`
- Calls: `renderer.draw(..., gradientDayNight = config.gradientDayNight)`

- [ ] **Step 1: Update `WatchClockView.kt`**

In `WatchClockView.kt`, pass `gradientDayNight = config.gradientDayNight` to `renderer.draw(...)`.

- [ ] **Step 2: Update `WatchWallpaperService.kt`**

In `WatchWallpaperService.kt`, pass `gradientDayNight = config.gradientDayNight` to `renderer.draw(...)`.

- [ ] **Step 3: Update `WatchAppWidgetProvider.kt`**

In `WatchAppWidgetProvider.kt`, pass `gradientDayNight = config.gradientDayNight` to `renderer.draw(...)`.

- [ ] **Step 4: Run full unit test suite**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew test`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
rtk git add app/src/main/java/com/watch1/app/WatchClockView.kt app/src/main/java/com/watch1/app/WatchWallpaperService.kt app/src/main/java/com/watch1/app/WatchAppWidgetProvider.kt
rtk git commit -m "feat: propagate gradientDayNight setting to clock view, wallpaper, and widgets"
```

---

### Task 5: Add Switch to Settings UI and Wire in `MainActivity`

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/java/com/watch1/app/MainActivity.kt`

**Interfaces:**
- Consumes: `@+id/switchGradientDayNight`, `@string/switch_gradient_day_night`
- Interacts: `LocationHelper.getGradientDayNight(this)`, `LocationHelper.saveGradientDayNight(this, isChecked)`

- [ ] **Step 1: Add `SwitchMaterial` to `activity_main.xml`**

In `app/src/main/res/layout/activity_main.xml`, inside the "Parameters" card (e.g. before `switchRadialOrientation` or after `switchShowBrandLogo`):
```xml
                    <!-- Day/Night Gradient -->
                    <com.google.android.material.switchmaterial.SwitchMaterial
                        android:id="@+id/switchGradientDayNight"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:paddingHorizontal="16dp"
                        android:paddingVertical="12dp"
                        android:text="@string/switch_gradient_day_night"
                        android:textColor="#FFFFFF"
                        android:textSize="15sp"
                        app:switchPadding="16dp" />

                    <View android:layout_width="match_parent" android:layout_height="1dp" android:background="#1E2333" android:layout_marginHorizontal="16dp"/>
```

- [ ] **Step 2: Wire switch in `MainActivity.kt`**

In `MainActivity.kt`:
```kotlin
        val switchGradient = findViewById<SwitchMaterial>(R.id.switchGradientDayNight)
        switchGradient.isChecked = LocationHelper.getGradientDayNight(this)
        switchGradient.setOnCheckedChangeListener { _, isChecked ->
            LocationHelper.saveGradientDayNight(this, isChecked)
            clockView.refreshSettings()
        }
```

- [ ] **Step 3: Run test suite**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew test`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
rtk git add app/src/main/res/layout/activity_main.xml app/src/main/java/com/watch1/app/MainActivity.kt
rtk git commit -m "feat: add day/night gradient switch to settings screen"
```

---

### Task 6: Comprehensive Verification

**Files:**
- Test: `app/src/test/java/com/watch1/app/WatchDialRendererTest.kt`

- [ ] **Step 1: Add edge cases tests in `WatchDialRendererTest.kt`**
  - Polar Day
  - Polar Night
  - Various sunset/sunrise permutations (e.g. northern vs southern hemisphere)

- [ ] **Step 2: Run full build and unit tests**

Run: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew test`
Expected: BUILD SUCCESSFUL (all unit tests passing)

- [ ] **Step 3: Commit**

```bash
rtk git add app/src/test/java/com/watch1/app/WatchDialRendererTest.kt
rtk git commit -m "test: add comprehensive unit tests for day/night gradient calculation"
```
