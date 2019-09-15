package com.jinsoft77.dublinbussoap

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.jinsoft77.dublinbussoap.entities.Destination
import kotlinx.android.synthetic.main.activity_stop_maps.*



class NearMeMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    val TAG = this.toString()
    private lateinit var mMap: GoogleMap
    private var myLocationArray: DoubleArray = DoubleArray(2) { i -> i * 0.00}
    var myNearStopsArray: IntArray? = null
    var markerDataMap: HashMap<Marker, Int> = HashMap()
    var filteredDestinationList :ArrayList<Destination> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_stop_maps)

        loadActivity(savedInstanceState)
        Log.w(TAG, "1 onCreate finished -- ")
    }

    fun loadActivity (savedInstanceState: Bundle?) : Unit {

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                // extra is null
            } else {
                myLocationArray = extras.getDoubleArray("myLocationArray")
                myNearStopsArray = extras.getIntArray("myNearStopsArray")
                filteredDestinationList = extras.getParcelableArrayList("filteredDestinationList")
            }
        } else {
            myLocationArray = savedInstanceState.getSerializable("myLocationArray") as DoubleArray
            myNearStopsArray = savedInstanceState.getSerializable("myNearStopsArray") as IntArray
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        floatingActionButton2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivityRaw", "Can't find style.", e)
        }

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(myLocationArray!![0], myLocationArray!![1]))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(myLocationArray!![0], myLocationArray!![1]), 16.5f))
        mMap.uiSettings.isMapToolbarEnabled = false // remove navigation menu when Marker clicked.

        mMap.setOnMarkerClickListener { marker ->
            if (markerDataMap.get(marker) != null) {
                val stopNumber = markerDataMap.get(marker)
                // Toast.makeText(this@NearMeMapsActivity, string, Toast.LENGTH_SHORT).show()
                val i = Intent(this@NearMeMapsActivity, BusStopInfoActivity::class.java)
                i.putExtra("stopNo", stopNumber)
                startActivity(i)
            }
            true
        }

        SoapServiceGetMyNearDestinationsLatLong().execute(filteredDestinationList)

        Log.w(TAG, "2 onMapReady finised -- ")
    }

    inner class SoapServiceGetMyNearDestinationsLatLong : AsyncTask<ArrayList<Destination>, Void, ArrayList<Destination>>() {
        override fun doInBackground(vararg params: ArrayList<Destination>): ArrayList<Destination> {
            Log.w(TAG, "3 doInBackground finished -- ")
            return params[0]
        }

        override fun onPostExecute(filteredDestinationList: ArrayList<Destination>) {
            super.onPostExecute(filteredDestinationList)

            filteredDestinationList.forEach{
                val location = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                val marker = mMap.addMarker(MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                markerDataMap[marker] = it.stopNumber.toInt()
            }

            Log.w(TAG, "4 onPostExecute finished -- ")
        }
    }
}
