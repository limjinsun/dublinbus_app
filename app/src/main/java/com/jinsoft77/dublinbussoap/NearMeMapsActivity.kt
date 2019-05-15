package com.jinsoft77.dublinbussoap

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.content.res.Resources
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.android.synthetic.main.activity_stop_maps.*

class NearMeMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val TAG = this.toString()

    private lateinit var mMap: GoogleMap
    var myLocationArray: DoubleArray = DoubleArray(2){ i -> i * 0.0 }

    var myNearStopsArray: Array<String>? = null
    var myNearStopsDataMap: HashMap<String, DoubleArray> = HashMap()

    var markerDataMap: HashMap<Marker, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_maps)

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                // extra is null
            } else {
                myLocationArray = extras.getDoubleArray("myLocationArray")
                myNearStopsArray = extras.getStringArray("myNearStopsArray")
            }
        } else {
            myLocationArray = savedInstanceState.getSerializable("myLocationArray") as DoubleArray
            myNearStopsArray = savedInstanceState.getSerializable("myNearStopsArray") as Array<String>
        }

        SoapServiceGetMyNearDestinationsLatLong().execute(myNearStopsArray)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
                .position(LatLng(myLocationArray[0], myLocationArray[1]))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(myLocationArray[0], myLocationArray[1]), 16.5f))
        mMap.uiSettings.isMapToolbarEnabled = false // remove navigation menu when Marker clicked.

        mMap.setOnMarkerClickListener { marker ->
            if (markerDataMap.get(marker) != null) {
                var stopNumber = markerDataMap.get(marker)
                // Toast.makeText(this@NearMeMapsActivity, string, Toast.LENGTH_SHORT).show()
                var i: Intent = Intent(this@NearMeMapsActivity, BusStopInfoActivity::class.java)
                i.putExtra("stopNo", stopNumber)
                startActivity(i)
            }
            true
        }
    }

    inner class SoapServiceGetMyNearDestinationsLatLong : AsyncTask<Array<String>, Void, HashMap<String, DoubleArray>>() {

        override fun doInBackground(vararg params: Array<String>): HashMap<String, DoubleArray> {
            // Log.wtf(TAG, "SoapServiceGet5Destinations doInB called -- ")

            for (i in 1..myNearStopsArray!!.size) {
                var latLong: DoubleArray = DublinBusAPICall().getDestinationsLatLong(myNearStopsArray!![i - 1])
                myNearStopsDataMap.put(myNearStopsArray!![i - 1], latLong)
            }

            return myNearStopsDataMap
        }

        override fun onPostExecute(myNearStopsDataMap: HashMap<String, DoubleArray>) {
            super.onPostExecute(myNearStopsDataMap)

            for (i in 1..myNearStopsDataMap.size) {
                var latiLontiDoubleArray: DoubleArray = myNearStopsDataMap[myNearStopsArray!![i - 1]]!!
                val location = LatLng(latiLontiDoubleArray[0], latiLontiDoubleArray[1])
                var marker = mMap.addMarker(MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                markerDataMap.put(marker, myNearStopsArray!![i - 1])
            }

            floatingActionButton2.setOnClickListener {
                var i: Intent = Intent(this@NearMeMapsActivity, MainActivity::class.java)
                startActivity(i)
            }
        }

    }
}
