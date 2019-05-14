package com.jinsoft77.dublinbussoap

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.jinsoft77.dublinbussoap.entities.Bus

class RecyclerViewAdapter(
    context: Context,
    busList: MutableList<Bus>
    ) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    var typeFace: Typeface = Typeface.createFromAsset(context.assets,"fonts/NTR-Regular.ttf")
    val TAG:String = this.toString()

    var context = context
    var busList = busList

    // 2. create veiw holder using subclass
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        Log.wtf(TAG, "** onCreateViewHolder called**")
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.each_bus_info_item, parent, false)
        var viewHolder = ViewHolder(view)
        return viewHolder
    }

    // 1. Count how many view needed
    override fun getItemCount(): Int {
        Log.wtf(TAG, "** getItemCount called**")
        return busList.size
    }

    // 3. binding element to View
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
         Log.wtf(TAG, "onBindViewHolder method called")

        viewHolder.busNo.text = busList[position].MonitoredVehicleJourney_PublishedLineName
        viewHolder.destination.text = busList[position].MonitoredVehicleJourney_DestinationName
        viewHolder.duetime.text = busList[position].Due_Time + " min"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var busNo: TextView = itemView.findViewById(R.id.busNo_textview)
        var destination: TextView = itemView.findViewById(R.id.destination_textview)
        var duetime: TextView = itemView.findViewById(R.id.duetime_textview)
        var parentLayout: RelativeLayout = itemView.findViewById(R.id.bus_info_layout)

        init{
            busNo.typeface = typeFace
            destination.typeface = typeFace
            duetime.typeface = typeFace
        }
    }

}

//이 순서로 실행이 됨.
//2019-05-14 21:55:54.965 19270-19270/com.jinsoft77.dublinbussoap E/com.jinsoft77.dublinbussoap.RecyclerViewAdapter@be2b09f: ** getItemCount called**
//2019-05-14 21:55:54.974 19270-19270/com.jinsoft77.dublinbussoap E/com.jinsoft77.dublinbussoap.RecyclerViewAdapter@be2b09f: ** onCreateViewHolder called**
//2019-05-14 21:55:54.983 19270-19270/com.jinsoft77.dublinbussoap E/com.jinsoft77.dublinbussoap.RecyclerViewAdapter@be2b09f: onBindViewHolder method called
