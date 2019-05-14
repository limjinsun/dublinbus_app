package com.jinsoft77.dublinbussoap.entities

class Bus {
    lateinit var MonitoredStopVisit_MonitoringRef: String
    lateinit var MonitoredVehicleJourney_LineRef: String
    lateinit var MonitoredVehicleJourney_DirectionRef: String
    lateinit var MonitoredVehicleJourney_PublishedLineName: String
    lateinit var MonitoredVehicleJourney_DestinationRef: String
    lateinit var MonitoredVehicleJourney_DestinationName: String
    lateinit var MonitoredCall_ExpectedArrivalTime: String
    lateinit var MonitoredCall_ExpectedDepartureTime: String
    lateinit var Timestamp: String
    lateinit var Due_Time: String
}