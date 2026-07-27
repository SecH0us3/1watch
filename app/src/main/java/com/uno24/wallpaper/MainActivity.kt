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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
    }

    private lateinit var tvThemeName: TextView
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLocationPermission()

        tvThemeName = findViewById(R.id.tvCurrentThemeName)
        val cardPreview = findViewById<MaterialCardView>(R.id.cardThemePreview)

        updateThemeDisplay()

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY) && abs(diffX) > 80 && abs(velocityX) > 80) {
                    val currentTheme = LocationHelper.getSavedTheme(this@MainActivity)
                    val newTheme = if (diffX < 0) currentTheme.next() else currentTheme.previous()
                    LocationHelper.saveTheme(this@MainActivity, newTheme)
                    updateThemeDisplay()
                    Toast.makeText(this@MainActivity, "Theme: ${newTheme.title}", Toast.LENGTH_SHORT).show()
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

        cardPreview.setOnTouchListener(touchListener)
        findViewById<View>(R.id.mainRootLayout).setOnTouchListener(touchListener)

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

    private fun updateThemeDisplay() {
        val currentTheme = LocationHelper.getSavedTheme(this)
        tvThemeName.text = currentTheme.title
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
