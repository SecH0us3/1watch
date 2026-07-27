package com.botta.uno24

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSetWallpaper = findViewById<Button>(R.id.btnSetWallpaper)
        btnSetWallpaper.setOnClickListener {
            launchWallpaperPicker()
        }
    }

    private fun launchWallpaperPicker() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, Uno24WallpaperService::class.java)
            )
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to launch live wallpaper chooser", Toast.LENGTH_SHORT).show()
        }
    }
}
