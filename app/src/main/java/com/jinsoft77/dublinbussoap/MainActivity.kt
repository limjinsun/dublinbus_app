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
import kotlinx.coroutines.*

class MainActivity: AppCompatActivity() {

    var TAG = this.toString()
    var MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        get_ACCESS_COARSE_LOCATION_Permission()

        var typeFace: Typeface = Typeface.createFromAsset(getAssets(),"fonts/NTR-Regular.ttf")
        tv_loading.typeface = typeFace
        progressBar.visibility = View.VISIBLE

        //coroutineGetAllDestination()
        //coroutineGetAllDestinationWithContext()
        SoapServiceGetAllDestinations().execute()

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {

        Log.wtf("onRequestPermissionsResult","method called")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                Log.wtf("onRequestPermissionsResult",MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION.toString())
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.w("grantResults",grantResults.toString())
                    get_ACCESS_FINE_LOCATION_permission()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun get_ACCESS_COARSE_LOCATION_Permission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.wtf("ContextCompat.checkSelfPermission - ACCESS_COARSE_LOCATION", "false")

            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Please Turn on GPS at Setting for This app", Toast.LENGTH_SHORT).show()
            } else {
                // No explanation needed, we can request the permission.
                Log.wtf("ContextCompat.checkSelfPermission - ACCESS_COARSE_LOCATION", "Asking permission")
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                )
            }
        } else {
            // Permission has already been granted
        }
    }

    private fun get_ACCESS_FINE_LOCATION_permission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.wtf("ContextCompat.checkSelfPermission - ACCESS_FINE_LOCATION", "false")

            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Please Turn on GPS at Setting for This app", Toast.LENGTH_SHORT).show()
            } else {
                // No explanation needed, we can request the permission.
                Log.i("ContextCompat.checkSelfPermission - ACCESS_FINE_LOCATION", "Asking permission")
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                )

                //coroutineGetAllDestination()
                //coroutineGetAllDestinationWithContext()
                SoapServiceGetAllDestinations().execute()
            }
        } else {
            // Permission has already been granted
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

    private fun isPermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return false
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return false
        return true
    }

    inner class SoapServiceGetAllDestinations: AsyncTask<Void, Void, MutableList<Destination>>() {

        override fun doInBackground(vararg params: Void?): MutableList<Destination>? {
            var destinationsList: MutableList<Destination>? = DublinBusAPICall().getAllDestinations()
            Log.wtf(TAG,"This thread is woring on " + Thread.currentThread().name)
            return destinationsList
        }

        override fun onPostExecute(destinationsList: MutableList<Destination>?) {
            Log.wtf(TAG,"This second block is woring on " + Thread.currentThread().name)
            super.onPostExecute(destinationsList)

            // KDtree is algorithm for finding nearest stop.
            var kdTree: KDTree = fillKdTree(destinationsList)
            if(isPermissionGranted()){
                var myLocationArray: DoubleArray = getLatitudeLongitude()
                // My 10 near stops.
                var myNearStops: List<String> = kdTree.nearest(myLocationArray, 10).map{ it.toString() }
                var myNearStopsArray= myNearStops.toTypedArray()

                var i : Intent = Intent(this@MainActivity, NearMeMapsActivity::class.java)
                i.putExtra("myLocationArray", myLocationArray)
                i.putExtra("myNearStopsArray", myNearStopsArray)
                progressBar.visibility = View.GONE
                startActivity(i)

            } else {
                get_ACCESS_COARSE_LOCATION_Permission()
            }
        }
    }

    private fun fillKdTree(destinationsList: MutableList<Destination>?): KDTree {
        var kdTree: KDTree = KDTree(2)
        if (destinationsList != null) {
            for (destination in destinationsList) {
                val array: DoubleArray = doubleArrayOf(0.00, 0.00)
                array[0] = destination.latitude.toDouble()
                array[1] = destination.longitude.toDouble()
                // Put every stop location(latitude, longitude) into KDtree.
                kdTree.insert(array, destination.stopNumber)
            }
        }
        return kdTree
    }


    /**

    fun coroutineGetAllDestinationWithContext() = runBlocking{

        var deferred = withContext(Dispatchers.IO) {
            Log.wtf(TAG,"This first block is woring on " + Thread.currentThread().name)
            DublinBusAPICall().getAllDestinations()
        }

        var destinationsList: MutableList<Destination>? = mutableListOf()
        destinationsList = deferred as MutableList<Destination>

        // Outside of runBlocking to show that it's running in the blocked main thread
        Log.wtf(TAG,"This second block is woring on " + Thread.currentThread().name)
        var kdTree: KDTree = fillKdTree(destinationsList)

        if(isPermissionGranted()){
            var myLocationArray: DoubleArray = getLatitudeLongitude()

            // My 10 near stops.
            var myNearStops: List<String> = kdTree.nearest(myLocationArray, 10).map{ it.toString() }
            var myNearStopsArray= myNearStops.toTypedArray()

            var i : Intent = Intent(this@MainActivity, NearMeMapsActivity::class.java)
            i.putExtra("myLocationArray", myLocationArray)
            i.putExtra("myNearStopsArray", myNearStopsArray)
            progressBar.visibility = View.GONE
            startActivity(i)

        } else {
            get_ACCESS_COARSE_LOCATION_Permission()
        }
        // It still runs only after the runBlocking is fully executed.
    }

    **/

    /**

    fun coroutineGetAllDestination(){
        var destinationsList: MutableList<Destination>? = mutableListOf()

        runBlocking(Dispatchers.IO) {
            destinationsList = DublinBusAPICall().getAllDestinations()
            Log.wtf(TAG,"This thread is woring on " + Thread.currentThread().name)
        }

        // Outside of runBlocking to show that it's running in the blocked main thread
        Log.wtf(TAG,"This second block is woring on " + Thread.currentThread().name)
        var kdTree: KDTree = fillKdTree(destinationsList)

        if(isPermissionGranted()){
            var myLocationArray: DoubleArray = getLatitudeLongitude()

            // My 10 near stops.
            var myNearStops: List<String> = kdTree.nearest(myLocationArray, 10).map{ it.toString() }
            var myNearStopsArray= myNearStops.toTypedArray()

            var i : Intent = Intent(this@MainActivity, NearMeMapsActivity::class.java)
            i.putExtra("myLocationArray", myLocationArray)
            i.putExtra("myNearStopsArray", myNearStopsArray)
            progressBar.visibility = View.GONE
            Log.wtf(TAG,"progressbar gone")
            startActivity(i)

        } else {
            get_ACCESS_COARSE_LOCATION_Permission()
        }
        // It still runs only after the runBlocking is fully executed.
    }

    **/


}
