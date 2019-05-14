package com.jinsoft77.dublinbussoap

import android.util.Log
import com.jinsoft77.dublinbussoap.entities.Bus
import com.jinsoft77.dublinbussoap.entities.Destination
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import android.os.Build
import android.support.annotation.RequiresApi
import java.time.*
import java.time.format.DateTimeFormatter

class DublinBusAPICall {

    val TAG = this.toString()

    //@RequiresApi(Build.VERSION_CODES.O)
    fun getRealTimeStopData(stopId : String?, forceRefresh: String?) : MutableList<Bus> {

        var busList: MutableList<Bus> = mutableListOf()
        val METHOD_NAME = "GetRealTimeStopData"
        val SOAP_ACTION = Utils.SOAP_NAMESPACE + METHOD_NAME
        val soapObject = SoapObject(Utils.SOAP_NAMESPACE, METHOD_NAME)

        soapObject.addProperty("stopId", stopId)
        soapObject.addProperty("forceRefresh", forceRefresh)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.setOutputSoapObject(soapObject)

        envelope.dotNet = true

        val httpTransportSE = HttpTransportSE(Utils.SOAP_URL)

        try {
            httpTransportSE.call(SOAP_ACTION, envelope)
            val soapPrimitive: SoapObject = envelope.response as SoapObject

            // index 1 is Stopdata.
            var data: SoapObject = soapPrimitive.getProperty(1) as SoapObject
            // get to "Document Element"
            if(data.hasProperty("DocumentElement")){
                var documentElement: SoapObject = data.getProperty("DocumentElement") as SoapObject

                for(i in 1..documentElement.propertyCount) {
                    var obj = documentElement.getProperty(i-1) as SoapObject

                    var bus = Bus()
                    bus.MonitoredStopVisit_MonitoringRef = obj.getProperty("MonitoredStopVisit_MonitoringRef").toString()
                    bus.MonitoredVehicleJourney_LineRef = obj.getProperty("MonitoredVehicleJourney_LineRef").toString()
                    bus.MonitoredVehicleJourney_DirectionRef = obj.getProperty("MonitoredVehicleJourney_DirectionRef").toString()
                    bus.MonitoredVehicleJourney_PublishedLineName = obj.getProperty("MonitoredVehicleJourney_PublishedLineName").toString()
                    bus.MonitoredVehicleJourney_DestinationRef = obj.getProperty("MonitoredVehicleJourney_DirectionRef").toString()
                    bus.MonitoredVehicleJourney_DestinationName = obj.getProperty("MonitoredVehicleJourney_DestinationName").toString()
                    bus.MonitoredCall_ExpectedArrivalTime = obj.getProperty("MonitoredCall_ExpectedArrivalTime").toString().substring(0,19)
                    bus.MonitoredCall_ExpectedDepartureTime = obj.getProperty("MonitoredCall_ExpectedDepartureTime").toString().substring(0,19)
                    bus.Timestamp = obj.getProperty("Timestamp").toString().substring(0,19)

                    var dublinZone : ZoneId = ZoneId.of("Europe/Dublin")

                    var expectedArrivalLocalDateTime: LocalDateTime = LocalDateTime.parse(bus.MonitoredCall_ExpectedArrivalTime, DateTimeFormatter.ISO_DATE_TIME)
                    var epochSecondOfArriavl : Long = expectedArrivalLocalDateTime.atZone(dublinZone).toEpochSecond()

                    var timestampLocalDateTime: LocalDateTime = LocalDateTime.parse(bus.Timestamp, DateTimeFormatter.ISO_DATE_TIME)
                    var epochSecondTimestamp: Long = timestampLocalDateTime.atZone(dublinZone).toEpochSecond()

                    var timeGap = (epochSecondOfArriavl - epochSecondTimestamp) / 60
                    bus.Due_Time = timeGap.toString()

                    // Log.wtf(TAG, "MonitoredVehicleJourney_PublishedLineName : " + bus.MonitoredVehicleJourney_PublishedLineName)
                    Log.wtf(TAG, "epochSecondOfArriavl : " + epochSecondOfArriavl)
                    Log.wtf(TAG, "epochSecondTimestamp : " + epochSecondTimestamp)
                    Log.wtf(TAG, "time-gap : " + timeGap )

                    Log.wtf(TAG, "toEpochSecond : " + expectedArrivalLocalDateTime.atZone(ZoneId.of("Europe/Dublin")).toEpochSecond())
                    Log.wtf(TAG, "getRealTimeStopData called" + bus.toString())

                    busList.add(bus)
                }

            } else {
                Log.wtf(TAG,"DocumentElement not found")
                Log.v(TAG,"busList size : " + busList.size.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return busList
    }

    fun getStopDataByRoute(route : String?) : String {
        val METHOD_NAME = "GetStopDataByRoute"
        var result = ""
        val SOAP_ACTION = Utils.SOAP_NAMESPACE + METHOD_NAME
        val soapObject = SoapObject(Utils.SOAP_NAMESPACE, METHOD_NAME)

        soapObject.addProperty("route", route)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.setOutputSoapObject(soapObject)

        envelope.dotNet = true

        val httpTransportSE = HttpTransportSE(Utils.SOAP_URL)

        try {
            httpTransportSE.call(SOAP_ACTION, envelope)
            val soapPrimitive = envelope.response
            result = soapPrimitive.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun getAllDestinations() : MutableList<Destination>? {
        var destinationsList : MutableList<Destination> = mutableListOf()
        val METHOD_NAME = "GetAllDestinations"
        val SOAP_ACTION = Utils.SOAP_NAMESPACE + METHOD_NAME
        val soapObject = SoapObject(Utils.SOAP_NAMESPACE, METHOD_NAME)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.setOutputSoapObject(soapObject)

        envelope.dotNet = true

        val httpTransportSE = HttpTransportSE(Utils.SOAP_URL)

        try {
            httpTransportSE.call(SOAP_ACTION, envelope)
            val obj = envelope.response as SoapObject

            if(obj.hasProperty("Destinations")) {
                var destinations = obj.getProperty("Destinations") as SoapObject
                for (i in 1..destinations.propertyCount) {
                    var destination = destinations.getProperty(i-1) as SoapObject
                    var mDestination = Destination()
                    mDestination.stopNumber = destination.getProperty("StopNumber").toString()
                    mDestination.longitude = destination.getProperty("Longitude").toString()
                    mDestination.latitude = destination.getProperty("Latitude").toString()
                    mDestination.description = destination.getProperty("Description").toString()

                    destinationsList.add(mDestination)
                }
            }

            Log.wtf(TAG, "getAllDestinations called " + "destinationList.size : " + destinationsList.size.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return destinationsList
    }

}