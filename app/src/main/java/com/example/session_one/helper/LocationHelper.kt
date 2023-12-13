package com.example.session_one.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar

import java.util.Locale

class LocationHelper private constructor(){

    private val TAG = "LocationHelper"
    var locationPermissionGranted = false
    val REQUEST_LOCATION_CODE = 101
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest
    private val UPDATE_INTERVAL_IN_MILLISECONDS = 50000 //milliseconds


    companion object{
        val instance = LocationHelper()
    }

     init {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_IN_MILLISECONDS.toLong()).build()
    }

    private fun hasFineLocationPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            return false
        }
    }

    private fun hasCoarseLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun checkPermissions(context: Context) {
        if (hasFineLocationPermission(context) == true && hasCoarseLocationPermission(context) == true) {
            this.locationPermissionGranted = true
        }
        Log.d(TAG, "checkPermissions: Are location permissions granted? : " + this.locationPermissionGranted)

        if (this.locationPermissionGranted == false) {
            Log.d(TAG, "Permissions not granted, so requesting permission now...")
            requestLocationPermission(context)
        }
    }

    fun requestLocationPermission(context: Context) {
        val listOfRequiredPermission
                = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)

        ActivityCompat.requestPermissions(
            (context as Activity),
            listOfRequiredPermission,
            REQUEST_LOCATION_CODE)
    }

    fun getFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        }

        return fusedLocationProviderClient!!
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(context: Context, locationCallback: LocationCallback) {
        if (locationPermissionGranted) {
            try {
                this.getFusedLocationProviderClient(context)
                    .requestLocationUpdates(this.locationRequest,
                                            locationCallback,
                                            Looper.getMainLooper())
            } catch (ex: Exception) {
                Log.d(TAG, "stopLocationUpdates: Exception occurred while receiving location updates " + ex.localizedMessage)
            }
        } else {
            Log.d(TAG, "requestLocationUpdates: No Permission.. No Location updates")
        }
    }

    fun stopLocationUpdates(context: Context, locationCallback: LocationCallback) {
        try {
            this.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback)
        } catch (ex: Exception) {
            Log.e(TAG, "stopLocationUpdates: Exception occurred while stopping location updates " + ex.localizedMessage)
        }
    }

    fun performReverseGeocoding(address:String,context: Context):LatLng?{
        // forward geocoding: (street address --> latitude/longitude)

        // 1. Create an instance of the built in Geocoder class
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())


        // - get the address from the user interface


        Log.d(TAG, "Getting coordinates for ${address}")

        // 2. try to get the coordinate
        try {
            val searchResults:MutableList<Address>? = geocoder.getFromLocationName(address, 1)
            if (searchResults == null) {
                // Log.e --> outputs the message as an ERROR (red)
                // Log.d --> outputs the message as a DEBUG message
                Log.e(TAG, "searchResults variable is null")
                return null
            }

            // if not null, then we were able to get some results (and it is possible for the results to be empty)
            if (searchResults.size == 0) {
                Log.e("TAG","Search results are empty.")
            } else {
                // 3. Get the coordinate
                val foundLocation: Address = searchResults.get(0)
                // 4. output to screen
                var message = "Coordinates are: ${foundLocation.latitude}, ${foundLocation.longitude}"
                var latLng : LatLng = LatLng(foundLocation.latitude,foundLocation.longitude)
                return latLng

                Log.d(TAG, message)
            }


        } catch(ex:Exception) {
            Log.e(TAG, "Error encountered while getting coordinate location.")
            Log.e(TAG, ex.toString())
        }
        return null
    }

    fun performForwardGeocoding(context: Context,latLng: LatLng):Address? {
// 1. get the geocoder
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

        // 2. get the lat/lng from the UI


        // in real life you should use .toDoubleOrNull() and handle the null case
        val latAsDouble = latLng.latitude.toDouble()
        val lngAsDouble = latLng.longitude.toDouble()

        Log.d(TAG, "Getting address for ${latAsDouble}, ${lngAsDouble}")

        // 3. try to find a matching street address
        try {
            val searchResults: MutableList<Address>? =
                geocoder.getFromLocation(latAsDouble, lngAsDouble, 1)
            if (searchResults == null) {
                Log.e(TAG, "getting Street Address: searchResults is NULL ")
                return null
            }

            if (searchResults.size == 0) {
                Log.d(TAG, "Search results <= 0")
            } else {
                // 3. get the result
                val matchingAddress: Address = searchResults.get(0)

                // 4. output the properites of this address object
                Log.d(TAG, "Search results found")
                Log.d(TAG, "performForwardGeocoding: Postal Code " + matchingAddress.postalCode)
                Log.d(
                    TAG,
                    "performForwardGeocoding: Country Code " + matchingAddress.countryCode
                )
                Log.d(
                    TAG,
                    "performForwardGeocoding: Country Name " + matchingAddress.countryName
                )
                Log.d(TAG, "performForwardGeocoding: Locality " + matchingAddress.locality)
                Log.d(
                    TAG,
                    "performForwardGeocoding: getThoroughfare " + matchingAddress.thoroughfare
                )
                Log.d(
                    TAG,
                    "performForwardGeocoding: getSubThoroughfare " + matchingAddress.subThoroughfare
                )
                // output this information to the UI
                // street number, street address, city, province, country

                return matchingAddress;
            }


        } catch (ex: Exception) {
            Log.e(TAG, "Error encountered while getting coordinate location.")
            Log.e(TAG, ex.toString())
        }

return null
    }


     fun getDeviceLocation(
         context: Context,
         fusedLocationClient:FusedLocationProviderClient,
         callback: (LatLng)-> Unit
     ){
        // helper function to get device location
        // Before running fusedLocationClient.lastLocation, CHECK that the user gave you permission for FINE_LOCATION and COARSE_LOCATION
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
        return
        }

        // if YES, then go get the location, everything is fine ;)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }
                // Output the location
                val message = "The device is located at: ${location.latitude}, ${location.longitude}"
                Log.d(TAG, message)

                val latLng:LatLng = LatLng(location.latitude,location.longitude)
                callback(latLng)
            }
    }


}