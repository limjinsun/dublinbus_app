package com.jinsoft77.dublinbussoap

import android.graphics.Typeface
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.jinsoft77.dublinbussoap.entities.Bus
import kotlinx.android.synthetic.main.activity_bus_stop_info.*

class BusStopInfoActivity : AppCompatActivity() {

    val TAG = this.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stop_info)

        val stopNo: String?

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                stopNo = null
            } else {
                stopNo = extras.getString("stopNo")
            }
        } else {
            stopNo = savedInstanceState.getSerializable("stopNo") as String
        }

        when {
            // If there is no internet connection, showing error.
            !Utils.isConnected(this@BusStopInfoActivity) -> Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            else -> SoapServiceGetRealtimeStopData().execute(stopNo,"true")
        }

        floatingActionButton2.setOnClickListener {
            SoapServiceGetRealtimeStopData().execute(stopNo,"true")
        }

    }

    inner class SoapServiceGetRealtimeStopData: AsyncTask<String, Void, MutableList<Bus>>() {

        override fun doInBackground(vararg params: String?): MutableList<Bus> {
            // Log.wtf(TAG, "SoapServiceGetRealtimeStopData doInB called -- ")
            var busList: MutableList<Bus> = DublinBusAPICall().getRealTimeStopData(params[0],params[1])
            return busList
        }

        override fun onPostExecute(busList: MutableList<Bus>) {
            // Log.wtf(TAG, "onPostExecute called")
            super.onPostExecute(busList)
            initRecyclerView(busList)
        }
    }

    private fun initRecyclerView(busList: MutableList<Bus>) {
        // Log.wtf(TAG, "initRecyclerView: init recyclerview.")
        if(busList.size > 0){
            val recyclerView = findViewById<RecyclerView>(R.id.recyler_view)
            val adapter = RecyclerViewAdapter(this, busList )
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(LinearLayoutManager(this));
        } else {
            var typeFace: Typeface = Typeface.createFromAsset(getAssets(),"fonts/NTR-Regular.ttf")
            tv_noinfo.typeface = typeFace
            tv_noinfo.visibility = View.VISIBLE
        }

    }

}
