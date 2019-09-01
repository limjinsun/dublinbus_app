package com.jinsoft77.dublinbussoap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.jinsoft77.dublinbussoap.entities.Destination
import net.sf.javaml.core.kdtree.KDTree
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jinsoft77.dublinbussoap.locationhandle.LocationHandler
import com.jinsoft77.dublinbussoap.locationhandle.LocationResultListener
import com.jinsoft77.dublinbussoap.utility.DublinBusAPICall
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LocationResultListener {
    private var TAG = this.toString()
    private var MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: Int = 0

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val LOCATION_ACTIVITY_REQUEST_CODE = 1000
    private var locationHandler: LocationHandler? = null
    private var GLOBALDESTINATIONLIST : ArrayList<Destination> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        get_ACCESS_COARSE_LOCATION_Permission()
        val typeFace: Typeface = Typeface.createFromAsset(this.assets, "fonts/NTR-Regular.ttf")
        tv_loading.typeface = typeFace

        locationHandler = LocationHandler(this, this,
            LOCATION_ACTIVITY_REQUEST_CODE, LOCATION_PERMISSION_REQUEST_CODE)

        progressBar.visibility = View.VISIBLE
        SoapServiceGetAllDestinations().execute()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.wtf("onRequestPermissionsResult", "method called")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                Log.wtf(
                    "onRequestPermissionsResult",
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION.toString()
                )
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.w("grantResults", grantResults.toString())
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
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.wtf("ContextCompat.checkSelfPermission - ACCESS_COARSE_LOCATION", "false")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Please Turn on GPS at Setting for This app",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // No explanation needed, we can request the permission.
                Log.wtf(
                    "ContextCompat.checkSelfPermission - ACCESS_COARSE_LOCATION",
                    "Asking permission"
                )
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
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.wtf("ContextCompat.checkSelfPermission - ACCESS_FINE_LOCATION", "false")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Please Turn on GPS at Setting for This app",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // No explanation needed, we can request the permission.
                Log.i(
                    "ContextCompat.checkSelfPermission - ACCESS_FINE_LOCATION",
                    "Asking permission"
                )
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                )

                SoapServiceGetAllDestinations().execute()
            }
        } else {
            // Permission has already been granted
        }
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

    // AsyncTask를 상속받으면 백그라운드 스레드에서 실행됨.
    @SuppressLint("StaticFieldLeak")
    inner class SoapServiceGetAllDestinations : AsyncTask<Void, Void, ArrayList<Destination>>() {
        override fun doInBackground(vararg params: Void?): ArrayList<Destination>? {
            val destinationsList: ArrayList<Destination>? = DublinBusAPICall().getAllDestinations()
            Log.wtf(TAG, "This thread is woring on " + Thread.currentThread().name)
            return destinationsList
        }

        override fun onPostExecute(destinationsList: ArrayList<Destination>?) {
            Log.wtf(TAG, "This second block is woring on " + Thread.currentThread().name)
            super.onPostExecute(destinationsList)

            if (destinationsList != null) {
                GLOBALDESTINATIONLIST = destinationsList

                if (isPermissionGranted()) {
                    locationHandler!!.getUserLocation() // This will call getLocation() - callback
                } else {
                    get_ACCESS_COARSE_LOCATION_Permission()
                }
            }
        }
    }

    private fun fillKdTree(destinationsList: MutableList<Destination>?): KDTree {
        val kdTree = KDTree(2)
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

    override fun getLocation(location: Location) {
        // KDtree is algorithm for finding nearest stop.
        val kdTree: KDTree = fillKdTree(GLOBALDESTINATIONLIST)
        val myLocationArray = doubleArrayOf(location.latitude, location.longitude)
        val myNearStopsArray: IntArray = kdTree.nearest(myLocationArray, 10).map { it.toString().toInt() }.stream().mapToInt { it.toInt() }.toArray()
        val list = GLOBALDESTINATIONLIST.filter { destination -> myNearStopsArray.contains(destination.stopNumber.toInt()) }
        val filteredDestinationList = ArrayList(list)

        val i = Intent(this@MainActivity, NearMeMapsActivity::class.java)
        i.putExtra("myLocationArray", myLocationArray)
        i.putExtra("myNearStopsArray", myNearStopsArray)
        i.putParcelableArrayListExtra("filteredDestinationList", filteredDestinationList)
        progressBar.visibility = View.GONE

        startActivity(i)
    }
}

