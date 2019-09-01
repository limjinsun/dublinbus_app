package com.jinsoft77.dublinbussoap

import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinsoft77.dublinbussoap.utility.DublinBusAPICall
import com.jinsoft77.dublinbussoap.entities.Bus
import com.jinsoft77.dublinbussoap.utility.RecyclerViewAdapter
import com.jinsoft77.dublinbussoap.utility.Utils
import kotlinx.android.synthetic.main.activity_bus_stop_info.*
import java.lang.ref.WeakReference

class BusStopInfoActivity : AppCompatActivity() {

    val TAG = this.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stop_info)

        val stopNo: Int?

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                stopNo = null
            } else {
                stopNo = extras.getInt("stopNo")
                Log.wtf(TAG, "This stop number $stopNo")
            }
        } else {
            stopNo = savedInstanceState.getSerializable("stopNo") as Int
        }

        when {
            // If there is no internet connection, showing error.
            !Utils.isConnected(this@BusStopInfoActivity) -> Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            else -> SoapServiceGetRealtimeStopData(this@BusStopInfoActivity).execute(stopNo)
        }

        floatingActionButton2.setOnClickListener {
            SoapServiceGetRealtimeStopData(this@BusStopInfoActivity).execute(stopNo)
        }
    }

    class SoapServiceGetRealtimeStopData internal constructor(context: BusStopInfoActivity): AsyncTask<Int, Void, MutableList<Bus>>() {

        private val activityReference: WeakReference<BusStopInfoActivity> = WeakReference(context)

        override fun doInBackground(vararg params: Int?): MutableList<Bus> {
            // Log.wtf(TAG, "SoapServiceGetRealtimeStopData doInB called -- ")
            val busList: MutableList<Bus> = DublinBusAPICall().getRealTimeStopData(params[0])
            return busList
        }

        override fun onPostExecute(busList: MutableList<Bus>) {
            // Log.wtf(TAG, "onPostExecute called")
            super.onPostExecute(busList)
            initRecyclerView(busList)
        }

        private fun initRecyclerView(busList: MutableList<Bus>) {
            // Log.wtf(TAG, "initRecyclerView: init recyclerview.")
            val activity = activityReference.get()
            if(busList.size > 0){
                val recyclerView = activity!!.findViewById<RecyclerView>(R.id.recyler_view)
                val adapter = RecyclerViewAdapter(activity, busList)
                recyclerView.adapter = adapter;
                recyclerView.layoutManager = LinearLayoutManager(activity)
            } else {
                val typeFace: Typeface = Typeface.createFromAsset(activity!!.assets,"fonts/NTR-Regular.ttf")
                activity.tv_noinfo.typeface = typeFace
                activity.tv_noinfo.visibility = View.VISIBLE
            }
        }
    }
}
