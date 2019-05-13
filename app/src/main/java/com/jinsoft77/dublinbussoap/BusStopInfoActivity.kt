package com.jinsoft77.dublinbussoap

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.jinsoft77.dublinbussoap.entities.Bus

class BusStopInfoActivity : AppCompatActivity() {

    val TAG = this.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stop_info)

        val nearestStop: String?
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                nearestStop = null
            } else {
                nearestStop = extras.getString("nearestStop")
            }
        } else {
            nearestStop = savedInstanceState.getSerializable("nearestStop") as String
        }

        when {
            // If there is no internet connection, showing error.
            !Utils.isConnected(this@BusStopInfoActivity) -> Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            else -> SoapServiceGetRealtimeStopData().execute(nearestStop,"true")
        }

    }

    inner class SoapServiceGetRealtimeStopData: AsyncTask<String, Void, MutableList<Bus>>() {

        override fun doInBackground(vararg params: String?): MutableList<Bus> {
            Log.wtf(TAG, "SoapServiceGetRealtimeStopData doInB called -- ")
            var busList: MutableList<Bus> = DublinBusAPICall().getRealTimeStopData(params[0],params[1])
            return busList
        }

        override fun onPostExecute(busList: MutableList<Bus>) {
            Log.wtf(TAG, "onPostExecute called")
            super.onPostExecute(busList)
            initRecyclerView(busList)
        }
    }

    private fun initRecyclerView(busList: MutableList<Bus>) {
        Log.wtf(TAG, "initRecyclerView: init recyclerview.")

        val recyclerView = findViewById<RecyclerView>(R.id.recyler_view)
        val adapter = RecyclerViewAdapter(this, busList )
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(LinearLayoutManager(this));

//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
    }

//
//    inner class SoapServiceGetStopDataByRoute: AsyncTask<String, Void, String>() {
//
//        override fun doInBackground(vararg params: String?): String {
//            var result: String = DublinBusAPICall().getStopDataByRoute(params[0])
//            return result
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            try {
//                textView2.text = result
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

}
