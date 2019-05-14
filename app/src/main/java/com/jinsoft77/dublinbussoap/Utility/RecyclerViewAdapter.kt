package com.jinsoft77.dublinbussoap


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
import com.jinsoft77.dublinbussoap.entities.Bus

// 2. 뷰홀더에 필요한 멤버들을 store 가능한 객체들을 만들어주고, 생성자로 설정해주고,
// 리사이클러뷰 어댑터 클래스를 상속한다. 상속할때,type 을 내가 만들어준 뷰홀더로 설정해준다.
class RecyclerViewAdapter(
    context: Context,
    busList: MutableList<Bus>
    ) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    val TAG:String = this.toString()

    var context = context
    var busList = busList

    // public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    // 3. 뷰홀더가 어느 레이아웃에 보이게 되는지 설정해주고, 뷰홀더 를 만들어 주는 기능을 넣어준다.
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view: View = LayoutInflater.from(p0.context).inflate(R.layout.each_bus_info_item, p0, false)
        Log.wtf(TAG,"** onCreateViewHolder ** view" + view.toString())
        var viewHolder = ViewHolder(view)
        return viewHolder
    }

    // 4. 총 뷰의 사이즈. - 이것이 제대로 설정 안되면 뷰가 보이지 않음.
    override fun getItemCount(): Int {
        Log.wtf(TAG, "** getItemCount **" + busList.size.toString())
        return busList.size
    }

    // public void onBindViewHolder(ViewHolder holder, final int position)
    // 5. 뷰가 실제로 바인딩 될때 어떤 값을 참고해야되는지 설정해준다. int 값은 리스트의 포지션 값.
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Log.wtf(TAG, "onBindViewHolder method called")
        Log.wtf(TAG, "p1 : " + p1.toString())

        p0.busNo.text = busList[p1].MonitoredVehicleJourney_PublishedLineName
        p0.destination.text = busList[p1].MonitoredVehicleJourney_DestinationName
        p0.duetime.text = busList[p1].Due_Time
    }

    // 1. 마이 뷰 홀더 리사이클러뷰의 뷰홀더 클래스를 상속받는다. 리사이클뷰에 보여지게될 하나하나 요소들과 반응한다.
    // 표시될 레이아웃에 필요한 멤버들을 만들어 주고, 레이아웃에 맞게 설정해준다.
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var busNo: TextView = itemView.findViewById(R.id.busNo_textview)
        var destination: TextView = itemView.findViewById(R.id.destination_textview)
        var duetime: TextView = itemView.findViewById(R.id.duetime_textview)
        var parentLayout: RelativeLayout = itemView.findViewById(R.id.bus_info_layout)
    }

}