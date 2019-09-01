package com.jinsoft77.dublinbussoap.entities

import android.os.Parcel
import android.os.Parcelable

class Destination() : Parcelable {
    lateinit var stopNumber: String
    lateinit var longitude: String
    lateinit var latitude: String
    lateinit var description: String

    constructor(parcel: Parcel) : this() {
        stopNumber = parcel.readString()
        longitude = parcel.readString()
        latitude = parcel.readString()
        description = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stopNumber)
        parcel.writeString(longitude)
        parcel.writeString(latitude)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Destination> {
        override fun createFromParcel(parcel: Parcel): Destination {
            return Destination(parcel)
        }

        override fun newArray(size: Int): Array<Destination?> {
            return arrayOfNulls(size)
        }
    }
}