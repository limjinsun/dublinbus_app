package com.jinsoft77.dublinbussoap

import android.content.Context
import android.net.ConnectivityManager

class Utils {
    companion object {
        val SOAP_URL = "http://rtpi.dublinbus.ie/DublinBusRTPIService.asmx?"
        val SOAP_NAMESPACE = "http://dublinbus.ie/"

        fun isConnected(context: Context): Boolean {
            val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = mConnectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }
    }
}