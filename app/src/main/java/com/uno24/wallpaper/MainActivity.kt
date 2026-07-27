package com.uno24.wallpaper

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLocationPermission()

        val currentTheme = LocationHelper.getSavedTheme(this)
        val rgTheme = findViewById<RadioGroup>(R.id.rgThemeSelector)

        when (currentTheme) {
            DialTheme.CLASSIC_DARK -> rgTheme.check(R.id.rbClassicDark)
            DialTheme.SOLAR_GOLD -> rgTheme.check(R.id.rbSolarGold)
            DialTheme.MONOCHROME_LIGHT -> rgTheme.check(R.id.rbMonochromeLight)
            DialTheme.CYBERPUNK -> rgTheme.check(R.id.rbCyberpunk)
        }

        rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val selectedTheme = when (checkedId) {
                R.id.rbSolarGold -> DialTheme.SOLAR_GOLD
                R.id.rbMonochromeLight -> DialTheme.MONOCHROME_LIGHT
                R.id.rbCyberpunk -> DialTheme.CYBERPUNK
                else -> DialTheme.CLASSIC_DARK
            }
            LocationHelper.saveTheme(this, selectedTheme)
            Toast.makeText(this, "Theme updated: ${selectedTheme.title}", Toast.LENGTH_SHORT).show()
        }

        val switchUv = findViewById<SwitchMaterial>(R.id.switchUvArc)
        switchUv.isChecked = LocationHelper.getShowUv(this)
        switchUv.setOnCheckedChangeListener { _, isChecked ->
            LocationHelper.saveShowUv(this, isChecked)
            val msg = if (isChecked) "UV Activity Arc Enabled" else "UV Activity Arc Disabled"
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
                Toast.makeText(this, "Unable to launch wallpaper chooser", Toast.LENGTH_SHORT).show()
            }
        }
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
        }
    }
}
