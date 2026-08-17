package com.uno24.wallpaper

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001

        private data class ColorPreset(val titleResId: Int, val hex: String)

        private val COLOR_PRESETS = listOf(
            ColorPreset(R.string.color_midnight_navy, "#0A0F1D"),
            ColorPreset(R.string.color_charcoal_dark, "#121212"),
            ColorPreset(R.string.color_abyss_black, "#050811"),
            ColorPreset(R.string.color_forest_deep, "#07140B"),
            ColorPreset(R.string.color_deep_burgundy, "#14070D"),
            ColorPreset(R.string.color_titanium_slate, "#1C212D"),
            ColorPreset(R.string.color_warm_espresso, "#150E0A"),
            ColorPreset(R.string.color_pure_light, "#EAEAEA"),
            ColorPreset(R.string.color_warm_linen, "#F5F2EB"),
            ColorPreset(R.string.color_polar_ice, "#EBF3F9"),
            ColorPreset(R.string.color_sage_mint, "#E8EFE9"),
            ColorPreset(R.string.color_rose_silk, "#FAF0F2")
        )
    }

    private lateinit var clockView: Uno24ClockView
    private lateinit var tvThemeTitle: TextView
    private lateinit var tvSizeValue: TextView
    private lateinit var btnPickImage: MaterialButton
    private lateinit var btnPickColor: MaterialButton
    private lateinit var gestureDetector: GestureDetector

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val success = BackgroundImageHelper.saveImageFromUri(this, uri)
            if (success) {
                LocationHelper.saveBackgroundMode(this, BackgroundMode.CUSTOM_IMAGE)
                clockView.refreshSettings()
                updateBackgroundButtonsVisibility(BackgroundMode.CUSTOM_IMAGE)
            } else {
                Toast.makeText(this, getString(R.string.toast_bg_image_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockView = findViewById(R.id.clockView)
        tvThemeTitle = findViewById(R.id.tvThemeTitle)
        tvSizeValue = findViewById(R.id.tvSizeValue)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnPickColor = findViewById(R.id.btnPickColor)

        updateThemeTitle()
        updateSizeLabel()
        checkLocationPermission()

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY) && abs(diffX) > 80 && abs(velocityX) > 80) {
                    val currentTheme = LocationHelper.getSavedTheme(this@MainActivity)
                    val newTheme = if (diffX < 0) currentTheme.next() else currentTheme.previous()
                    LocationHelper.saveTheme(this@MainActivity, newTheme)
                    updateThemeTitle()
                    clockView.refreshSettings()
                    return true
                }
                return false
            }
        })

        val touchListener = View.OnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            v.performClick()
            true
        }

        clockView.setOnTouchListener(touchListener)
        findViewById<View>(R.id.mainRootLayout).setOnTouchListener(touchListener)

        // Background Mode Spinner setup
        val spinnerBgMode = findViewById<Spinner>(R.id.spinnerBgMode)
        val bgModes = BackgroundMode.values()
        val adapterBgMode = ArrayAdapter(this, R.layout.spinner_item, bgModes.map { it.getTitle(this) })
        adapterBgMode.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerBgMode.adapter = adapterBgMode
        
        val initialBgMode = LocationHelper.getBackgroundMode(this)
        spinnerBgMode.setSelection(initialBgMode.ordinal)
        updateBackgroundButtonsVisibility(initialBgMode)

        spinnerBgMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedBgMode = bgModes[position]
                updateBackgroundButtonsVisibility(selectedBgMode)
                if (selectedBgMode != LocationHelper.getBackgroundMode(this@MainActivity)) {
                    if (selectedBgMode == BackgroundMode.CUSTOM_IMAGE && !BackgroundImageHelper.hasCustomImage(this@MainActivity)) {
                        pickImageLauncher.launch("image/*")
                    } else if (selectedBgMode == BackgroundMode.CUSTOM_COLOR) {
                        LocationHelper.saveBackgroundMode(this@MainActivity, selectedBgMode)
                        clockView.refreshSettings()
                        showColorPickerDialog()
                    } else {
                        LocationHelper.saveBackgroundMode(this@MainActivity, selectedBgMode)
                        clockView.refreshSettings()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnPickColor.setOnClickListener {
            showColorPickerDialog()
        }

        // Hand Style Spinner setup
        val spinnerHandStyle = findViewById<Spinner>(R.id.spinnerHandStyle)
        val handStyles = HandStyle.values()
        val adapterHandStyle = ArrayAdapter(this, R.layout.spinner_item, handStyles.map { it.getTitle(this) })
        adapterHandStyle.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerHandStyle.adapter = adapterHandStyle
        spinnerHandStyle.setSelection(LocationHelper.getHandStyle(this).ordinal)
        spinnerHandStyle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStyle = handStyles[position]
                if (selectedStyle != LocationHelper.getHandStyle(this@MainActivity)) {
                    LocationHelper.saveHandStyle(this@MainActivity, selectedStyle)
                    clockView.refreshSettings()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Numeral Display Mode Spinner setup
        val spinnerDisplayMode = findViewById<Spinner>(R.id.spinnerDisplayMode)
        val modes = NumeralDisplayMode.values()
        val adapterMode = ArrayAdapter(this, R.layout.spinner_item, modes.map { it.getTitle(this) })
        adapterMode.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerDisplayMode.adapter = adapterMode
        spinnerDisplayMode.setSelection(LocationHelper.getNumeralDisplayMode(this).ordinal)
        spinnerDisplayMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMode = modes[position]
                if (selectedMode != LocationHelper.getNumeralDisplayMode(this@MainActivity)) {
                    LocationHelper.saveNumeralDisplayMode(this@MainActivity, selectedMode)
                    clockView.refreshSettings()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Numeral Style / System Spinner setup
        val spinnerNumeralStyle = findViewById<Spinner>(R.id.spinnerNumeralStyle)
        val styles = NumeralStyle.values()
        val adapterStyle = ArrayAdapter(this, R.layout.spinner_item, styles.map { it.getTitle(this) })
        adapterStyle.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerNumeralStyle.adapter = adapterStyle
        spinnerNumeralStyle.setSelection(LocationHelper.getNumeralStyle(this).ordinal)
        spinnerNumeralStyle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStyle = styles[position]
                if (selectedStyle != LocationHelper.getNumeralStyle(this@MainActivity)) {
                    LocationHelper.saveNumeralStyle(this@MainActivity, selectedStyle)
                    clockView.refreshSettings()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Font Size +/- Buttons setup
        findViewById<ImageButton>(R.id.btnSizeMinus).setOnClickListener {
            val currentScale = LocationHelper.getFontSizeScale(this)
            val newScale = (currentScale - 0.1f).coerceAtLeast(0.5f)
            LocationHelper.saveFontSizeScale(this, newScale)
            updateSizeLabel()
            clockView.refreshSettings()
        }

        findViewById<ImageButton>(R.id.btnSizePlus).setOnClickListener {
            val currentScale = LocationHelper.getFontSizeScale(this)
            val newScale = (currentScale + 0.1f).coerceAtMost(2.5f)
            LocationHelper.saveFontSizeScale(this, newScale)
            updateSizeLabel()
            clockView.refreshSettings()
        }

        // Numeral Font Spinner setup
        val spinnerNumeralFont = findViewById<Spinner>(R.id.spinnerNumeralFont)
        val fonts = NumeralFont.values()
        val adapterFont = ArrayAdapter(this, R.layout.spinner_item, fonts.map { it.getTitle(this) })
        adapterFont.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerNumeralFont.adapter = adapterFont
        spinnerNumeralFont.setSelection(LocationHelper.getNumeralFont(this).ordinal)
        spinnerNumeralFont.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFont = fonts[position]
                if (selectedFont != LocationHelper.getNumeralFont(this@MainActivity)) {
                    LocationHelper.saveNumeralFont(this@MainActivity, selectedFont)
                    clockView.refreshSettings()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Language Spinner setup
        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerAppLanguage)
        val languages = AppLanguage.values()
        val adapterLanguage = ArrayAdapter(this, R.layout.spinner_item, languages.map { it.displayName })
        adapterLanguage.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerLanguage.adapter = adapterLanguage
        spinnerLanguage.setSelection(LocationHelper.getAppLanguage(this).ordinal)
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                if (selectedLanguage != LocationHelper.getAppLanguage(this@MainActivity)) {
                    LocationHelper.saveAppLanguage(this@MainActivity, selectedLanguage)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val switchRadial = findViewById<SwitchMaterial>(R.id.switchRadialOrientation)
        switchRadial.isChecked = LocationHelper.getNumeralOrientation(this) == NumeralOrientation.RADIAL
        switchRadial.setOnCheckedChangeListener { _, isChecked ->
            val orientation = if (isChecked) NumeralOrientation.RADIAL else NumeralOrientation.UPRIGHT
            LocationHelper.saveNumeralOrientation(this, orientation)
            clockView.refreshSettings()
        }

        val switchUv = findViewById<SwitchMaterial>(R.id.switchUvArc)
        switchUv.isChecked = LocationHelper.getShowUv(this)
        switchUv.setOnCheckedChangeListener { _, isChecked ->
            LocationHelper.saveShowUv(this, isChecked)
            clockView.refreshSettings()
        }

        val switchDate = findViewById<SwitchMaterial>(R.id.switchShowDate)
        switchDate.isChecked = LocationHelper.getShowDate(this)
        switchDate.setOnCheckedChangeListener { _, isChecked ->
            LocationHelper.saveShowDate(this, isChecked)
            clockView.refreshSettings()
        }

        val switchUvIndex = findViewById<SwitchMaterial>(R.id.switchShowUvIndex)
        switchUvIndex.isChecked = LocationHelper.getShowUvIndex(this)
        switchUvIndex.setOnCheckedChangeListener { _, isChecked ->
            LocationHelper.saveShowUvIndex(this, isChecked)
            clockView.refreshSettings()
        }

        findViewById<Button>(R.id.btnAddWidget).setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val appWidgetManager = getSystemService(android.appwidget.AppWidgetManager::class.java)
                val provider = ComponentName(this, Uno24AppWidgetProvider::class.java)
                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    appWidgetManager.requestPinAppWidget(provider, null, null)
                }
            }
        }

        findViewById<Button>(R.id.btnSetWallpaper).setOnClickListener {
            try {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                    putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(this@MainActivity, Uno24WallpaperService::class.java)
                    )
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.toast_wallpaper_picker_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBackgroundButtonsVisibility(mode: BackgroundMode) {
        when (mode) {
            BackgroundMode.THEME_DEFAULT -> {
                btnPickImage.visibility = View.GONE
                btnPickColor.visibility = View.GONE
            }
            BackgroundMode.CUSTOM_IMAGE -> {
                btnPickImage.visibility = View.VISIBLE
                btnPickColor.visibility = View.GONE
            }
            BackgroundMode.CUSTOM_COLOR -> {
                btnPickImage.visibility = View.GONE
                btnPickColor.visibility = View.VISIBLE
            }
        }
    }

    private fun showColorPickerDialog() {
        val titles = COLOR_PRESETS.map { getString(it.titleResId) }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_title_pick_color))
            .setItems(titles) { _, which ->
                val preset = COLOR_PRESETS[which]
                val colorInt = Color.parseColor(preset.hex)
                LocationHelper.saveCustomColor(this, colorInt)
                LocationHelper.saveBackgroundMode(this, BackgroundMode.CUSTOM_COLOR)
                clockView.refreshSettings()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun updateThemeTitle() {
        val currentTheme = LocationHelper.getSavedTheme(this)
        tvThemeTitle.text = "←  ${currentTheme.getTitle(this)}  →"
    }

    private fun updateSizeLabel() {
        val scale = LocationHelper.getFontSizeScale(this)
        val percent = (scale * 100).roundToInt()
        tvSizeValue.text = "$percent%"
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        } else {
            LocationHelper.updateLocationIfPermitted(this) {
                if (::clockView.isInitialized) {
                    clockView.refreshSettings()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_LOCATION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            LocationHelper.updateLocationIfPermitted(this) {
                if (::clockView.isInitialized) {
                    clockView.refreshSettings()
                }
            }
        }
    }
}
