package com.example.data.sensor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class RealDeviceGpsManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onLocationUpdated: (lat: Double, lng: Double, address: String, accuracy: String) -> Unit
) : LocationListener {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private var isListening = false

    @SuppressLint("MissingPermission")
    fun requestRealLocation() {
        if (!hasLocationPermission()) return

        try {
            // Check last known locations first for instant response
            var bestLocation: Location? = null
            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (bestLocation == null && locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            if (bestLocation != null) {
                processLocation(bestLocation)
            }

            // Register continuous listener with battery-friendly 10s interval
            if (!isListening) {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10000L,
                        10f,
                        this
                    )
                    isListening = true
                } else if (locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        10000L,
                        10f,
                        this
                    )
                    isListening = true
                }
            }
        } catch (e: Exception) {
            // Fallback gracefully
        }
    }

    private fun processLocation(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val accuracyStr = if (location.hasAccuracy()) "±${location.accuracy.toInt()}m" else "Akurasi Tinggi"

        coroutineScope.launch(Dispatchers.IO) {
            var addressText = "Lokasi Terkini HP"
            try {
                val geocoder = Geocoder(context, Locale("id", "ID"))
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val subLocality = addr.subLocality ?: addr.locality ?: ""
                    val adminArea = addr.subAdminArea ?: addr.adminArea ?: ""
                    addressText = if (subLocality.isNotEmpty() && adminArea.isNotEmpty()) {
                        "$subLocality, $adminArea"
                    } else if (adminArea.isNotEmpty()) {
                        adminArea
                    } else {
                        addr.getAddressLine(0) ?: "Jakarta"
                    }
                }
            } catch (e: Exception) {
                addressText = "GPS Terhubung (Lat: ${String.format("%.4f", lat)}, Lng: ${String.format("%.4f", lng)})"
            }

            onLocationUpdated(lat, lng, addressText, accuracyStr)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onLocationChanged(location: Location) {
        processLocation(location)
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    fun stopLocationUpdates() {
        if (isListening) {
            try {
                locationManager?.removeUpdates(this)
                isListening = false
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}
