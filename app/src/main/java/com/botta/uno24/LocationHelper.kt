package com.botta.uno24

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

object LocationHelper {
    private const val PREFS_NAME = "Uno24Prefs"
    private const val KEY_LATITUDE = "latitude"
    private const val KEY_LONGITUDE = "longitude"

    const val DEFAULT_LAT = 52.52
    const val DEFAULT_LON = 13.405

    fun getSavedCoordinates(context: Context): Pair<Double, Double> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lat = prefs.getFloat(KEY_LATITUDE, DEFAULT_LAT.toFloat()).toDouble()
        val lon = prefs.getFloat(KEY_LONGITUDE, DEFAULT_LON.toFloat()).toDouble()
        return Pair(lat, lon)
    }

    fun saveCoordinates(context: Context, lat: Double, lon: Double) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat(KEY_LATITUDE, lat.toFloat())
            .putFloat(KEY_LONGITUDE, lon.toFloat())
            .apply()
    }

    @SuppressLint("MissingPermission")
    fun updateLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || loc.time > bestLocation.time) {
                        bestLocation = loc
                    }
                }
            } catch (e: SecurityException) {
                // Permission not granted or missing
            } catch (e: Exception) {
                // Provider disabled or unavailable
            }
        }

        bestLocation?.let {
            saveCoordinates(context, it.latitude, it.longitude)
        }
    }
}
