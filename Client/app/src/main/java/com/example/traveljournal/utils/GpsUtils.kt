package com.example.traveljournal.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.example.traveljournal.R
import com.example.traveljournal.presentation.view.MainActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class GpsUtils() : Service(), LocationListener {

    // flag for GPS status
    private lateinit var context:Context
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var isLocationEnabled = false
    var obtainedLocation:Location? = null
    private var latitude // latitude
            = 0.0
    private var longitude // longitude
            = 0.0
    private val REQUEST_CODE_LOCATION = 4000
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null
    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?
            // getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            // getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                return null
                // no network provider is enabled
            } else {
                isLocationEnabled = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    //check the network permission
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    if (locationManager != null) {
                        obtainedLocation = locationManager!!
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                }
                if (isGPSEnabled) {
                    if (obtainedLocation == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        if (locationManager != null) {
                            obtainedLocation = locationManager!!
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obtainedLocation
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GpsUtils)
        }
    }


    fun canGetLocation(): Boolean {
        return isLocationEnabled
    }

    private fun checkLocationSettings() {
     val locationRequest = LocationRequest.create()?.apply{
         interval = MIN_TIME_BW_UPDATES
         fastestInterval = 1000 * 30
         priority = LocationRequest.PRIORITY_HIGH_ACCURACY

     }
        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
                .setAlwaysShow(true)
        }
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val locationSettingTask: Task<LocationSettingsResponse>? = client.checkLocationSettings(builder?.build())

        locationSettingTask?.addOnCompleteListener {

            try {
                val response: LocationSettingsResponse? =
                        locationSettingTask.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                getLocation()

            } catch (exception: ApiException) {
                enableGPSDialog(exception)
            }
        }

    }
    private fun enableGPSDialog(exception: ApiException) {

        when (exception.statusCode) {

            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                /**Location Settings available but need to turn on*/
                try {
                    val resolvable = exception as ResolvableApiException
                    resolvable.startResolutionForResult(
                            context as Activity?,
                            REQUEST_CHECK_SETTINGS
                    )
                } catch (e: Exception) {
                    when (e) {
                        is IntentSender.SendIntentException -> {
                        }
                        is ClassCastException -> {
                        }
                    }
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                /**Location settings not available on the device.*/
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    /**
                     * User agreed to make required location settings changes.
                     * */
                    System.out.println("Zezwolono na lokalizacje")
                    getLocation()
                }
                Activity.RESULT_CANCELED -> {
                    /**
                     * User chose not to make required location settings changes.
                     * */
                }
            }
        }
    }


    override fun onLocationChanged(location: Location?) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }


    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 1 * 1 // 1 second
                .toLong()

        const val REQUEST_CHECK_SETTINGS = 0x1
    }

    fun init(context: Context) {
        this@GpsUtils.context = context
        checkLocationSettings()
    }
}