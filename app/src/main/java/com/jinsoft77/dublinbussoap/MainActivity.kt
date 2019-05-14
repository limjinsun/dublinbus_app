package com.jinsoft77.dublinbussoap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jinsoft77.dublinbussoap.entities.Destination
import net.sf.javaml.core.kdtree.KDTree
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var TAG = this.toString()
    var MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var typeFace: Typeface = Typeface.createFromAsset(getAssets(),"fonts/NTR-Regular.ttf")
        tv_loading.typeface = typeFace

        progressBar.visibility = View.VISIBLE

        getLocationPermission()
        SoapServiceGetAllDestinations().execute()

//        button.setOnClickListener{
//            SoapServiceGetAllDestinations().execute()
//        }

    }

    private fun isPermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return false
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return false

        return true
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.wtf("ContextCompat.checkSelfPermission - ACCESS_COARSE_LOCATION", "false")
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                Log.wtf("ContextCompat.checkSelfPermission - ACCESS_COARSE_LOCATION", "Asking permission")
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.wtf("ContextCompat.checkSelfPermission - ACCESS_FINE_LOCATION", "false")
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(this, "Please Turn on GPS at Setting for This app", Toast.LENGTH_SHORT).show()
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                Log.i("ContextCompat.checkSelfPermission - ACCESS_FINE_LOCATION", "Asking permission")
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {

        Log.wtf("onRequestPermissionsResult","method called")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                Log.wtf("onRequestPermissionsResult",MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION.toString())
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.w("grantResults",grantResults.toString())
                    getLocationPermission() // call permission again for "ACCESS_FINE_LOCATION"
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLatitudeLongitude (): DoubleArray {

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val longitude = location.longitude
        val latitude = location.latitude

        val myLocationArray: DoubleArray = doubleArrayOf(latitude,longitude)
        return myLocationArray
    }

    inner class SoapServiceGetAllDestinations: AsyncTask<Void, Void, MutableList<Destination>>() {

        override fun doInBackground(vararg params: Void?): MutableList<Destination>? {
            // Log.wtf(TAG,"SoapServiceGetAllDestinations called -- getting destinationList")
            var destinationsList: MutableList<Destination>? = DublinBusAPICall().getAllDestinations()
            return destinationsList
        }

        override fun onPostExecute(destinationsList: MutableList<Destination>?) {
            super.onPostExecute(destinationsList)

            // KDtree is algorithm for finding nearest stop.
            var kdTree: KDTree = KDTree(2)
            if (destinationsList != null) {
                for(destination in destinationsList){
                    val array: DoubleArray = doubleArrayOf(0.00, 0.00)
                    array[0] = destination.latitude.toDouble()
                    array[1] = destination.longitude.toDouble()
                    // Put every stop location(latitude, longitude) into KDtree.
                    kdTree.insert(array, destination.stopNumber)
                }
            }

            if(isPermissionGranted()){
                var myLocationArray: DoubleArray = getLatitudeLongitude()
                // find nearest stop.
                var closestStop = kdTree.nearest(myLocationArray).toString()
                var myNearStops: List<String>

                // My 10 near stops.
                myNearStops= kdTree.nearest(myLocationArray, 10).map{ it.toString()}
                var myNearStopsArray= myNearStops.toTypedArray()

                var i : Intent = Intent(this@MainActivity, NearMeMapsActivity::class.java)
                i.putExtra("myLocationArray", myLocationArray)
                i.putExtra("myNearStopsArray", myNearStopsArray)
                progressBar.visibility = View.GONE
                startActivity(i)

            } else {
                getLocationPermission()
            }

        }
    }
}
