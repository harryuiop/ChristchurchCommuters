package com.example.busapp.models

data class TransitRoutesResponse(
    val routes: List<Route>,
)

data class Route(
    val legs: List<Leg>,
)

data class Leg(
    val steps: List<Step>,
)

data class Step(
    val transitDetails: TransitDetails?,
)

data class TransitDetails(
    val stopDetails: StopDetails,
    val localizedValues: LocalizedValues,
    val headsign: String,
    val transitLine: TransitLine,
    val stopCount: Long,
)

data class StopDetails(
    val arrivalStop: ArrivalStop,
    val arrivalTime: String,
    val departureStop: DepartureStop,
    val departureTime: String,
)

data class ArrivalStop(
    val name: String,
    val location: Location,
)

data class Location(
    val latLng: LatLng,
)

data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

data class DepartureStop(
    val name: String,
    val location: Location2,
)

data class Location2(
    val latLng: LatLng2,
)

data class LatLng2(
    val latitude: Double,
    val longitude: Double,
)

data class LocalizedValues(
    val arrivalTime: ArrivalTime,
    val departureTime: DepartureTime,
)

data class ArrivalTime(
    val time: Time,
    val timeZone: String,
)

data class Time(
    val text: String,
)

data class DepartureTime(
    val time: Time2,
    val timeZone: String,
)

data class Time2(
    val text: String,
)

data class TransitLine(
    val agencies: List<Agency>,
    val name: String,
    val color: String,
    val nameShort: String,
    val textColor: String,
    val vehicle: Vehicle,
)

data class Agency(
    val name: String,
    val phoneNumber: String,
    val uri: String,
)

data class Vehicle(
    val name: Name,
    val type: String,
    val iconUri: String,
)

data class Name(
    val text: String,
)