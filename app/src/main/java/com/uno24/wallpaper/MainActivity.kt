package com.uno24.wallpaper

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
    }

    private lateinit var clockView: Uno24ClockView
    private lateinit var tvThemeTitle: TextView
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLocationPermission()

        clockView = findViewById(R.id.clockView)
        tvThemeTitle = findViewById(R.id.tvThemeTitle)

        updateThemeTitle()

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
                    clockView.invalidate()
                    Toast.makeText(this@MainActivity, "Тема: ${newTheme.title}", Toast.LENGTH_SHORT).show()
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

        // Numeral Display Mode Spinner setup
        val spinnerDisplayMode = findViewById<Spinner>(R.id.spinnerDisplayMode)
        val modes = NumeralDisplayMode.values()
        val adapterMode = ArrayAdapter(this, android.R.layout.simple_spinner_item, modes.map { it.title })
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDisplayMode.adapter = adapterMode
        spinnerDisplayMode.setSelection(LocationHelper.getNumeralDisplayMode(this).ordinal)
        spinnerDisplayMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMode = modes[position]
                if (selectedMode != LocationHelper.getNumeralDisplayMode(this@MainActivity)) {
                    LocationHelper.saveNumeralDisplayMode(this@MainActivity, selectedMode)
                    clockView.invalidate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Numeral Size Spinner setup
        val spinnerNumeralSize = findViewById<Spinner>(R.id.spinnerNumeralSize)
        val sizes = NumeralSize.values()
        val adapterSize = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizes.map { it.title })
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNumeralSize.adapter = adapterSize
        spinnerNumeralSize.setSelection(LocationHelper.getNumeralSize(this).ordinal)
        spinnerNumeralSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSize = sizes[position]
                if (selectedSize != LocationHelper.getNumeralSize(this@MainActivity)) {
                    LocationHelper.saveNumeralSize(this@MainActivity, selectedSize)
                    clockView.invalidate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Numeral Font Spinner setup
        val spinnerNumeralFont = findViewById<Spinner>(R.id.spinnerNumeralFont)
        val fonts = NumeralFont.values()
        val adapterFont = ArrayAdapter(this, android.R.layout.simple_spinner_item, fonts.map { it.title })
        adapterFont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNumeralFont.adapter = adapterFont
        spinnerNumeralFont.setSelection(LocationHelper.getNumeralFont(this).ordinal)
        spinnerNumeralFont.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFont = fonts[position]
                if (selectedFont != LocationHelper.getNumeralFont(this@MainActivity)) {
                    LocationHelper.saveNumeralFont(this@MainActivity, selectedFont)
                    clockView.invalidate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val switchRadial = findViewById<SwitchMaterial>(R.id.switchRadialOrientation)
        switchRadial.isChecked = LocationHelper.getNumeralOrientation(this) == NumeralOrientation.RADIAL
        switchRadial.setOnCheckedChangeListener { _, isChecked ->
            val orientation = if (isChecked) NumeralOrientation.RADIAL else NumeralOrientation.UPRIGHT
            LocationHelper.saveNumeralOrientation(this, orientation)
            clockView.invalidate()
            val msg = if (isChecked) "Цифры повернуты к центру" else "Цифры смотрят прямо"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        val switchRoman = findViewById<SwitchMaterial>(R.id.switchRomanNumerals)
        switchRoman.isChecked = LocationHelper.getNumeralStyle(this) == NumeralStyle.ROMAN
        switchRoman.setOnCheckedChangeListener { _, isChecked ->
            val style = if (isChecked) NumeralStyle.ROMAN else NumeralStyle.ARABIC
            LocationHelper.saveNumeralStyle(this, style)
            clockView.invalidate()
            val msg = if (isChecked) "Римские цифры включены" else "Арабские цифры включены"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        val switchUv = findViewById<SwitchMaterial>(R.id.switchUvArc)
        switchUv.isChecked = LocationHelper.getShowUv(this)
        switchUv.setOnCheckedChangeListener { _, isChecked ->
            LocationHelper.saveShowUv(this, isChecked)
            clockView.invalidate()
            val msg = if (isChecked) "УФ-активность включена" else "УФ-активность выключена"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Не удалось открыть выбор живых обоев", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateThemeTitle() {
        val currentTheme = LocationHelper.getSavedTheme(this)
        tvThemeTitle.text = "← Смахните для смены темы: ${currentTheme.title} →"
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
            LocationHelper.updateLocationIfPermitted(this)
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
            LocationHelper.updateLocationIfPermitted(this)
            clockView.invalidate()
        }
    }
}
