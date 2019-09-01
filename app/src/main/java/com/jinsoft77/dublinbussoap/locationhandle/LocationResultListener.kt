package com.jinsoft77.dublinbussoap.locationhandle

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}