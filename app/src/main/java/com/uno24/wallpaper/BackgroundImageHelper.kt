package com.uno24.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object BackgroundImageHelper {
    private const val BG_FILE_NAME = "custom_bg.png"
    private var cachedBitmap: Bitmap? = null
    private var lastModifiedTime: Long = 0L

    fun saveImageFromUri(context: Context, uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val file = File(context.filesDir, BG_FILE_NAME)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            cachedBitmap = null
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loadBitmap(context: Context): Bitmap? {
        val file = File(context.filesDir, BG_FILE_NAME)
        if (!file.exists()) return null

        if (cachedBitmap != null && file.lastModified() == lastModifiedTime) {
            return cachedBitmap
        }

        return try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            cachedBitmap = bitmap
            lastModifiedTime = file.lastModified()
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun hasCustomImage(context: Context): Boolean {
        return File(context.filesDir, BG_FILE_NAME).exists()
    }
}
