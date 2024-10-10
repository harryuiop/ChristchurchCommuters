package com.example.busapp.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransitRoutesResponse(
    val routes: List<Route>,
) : Parcelable

@Parcelize
data class Route(
    val legs: List<Leg>,
) : Parcelable

@Parcelize
data class Leg(
    val steps: List<Step>,
) : Parcelable

@Parcelize
data class Step(
    val transitDetails: TransitDetails?,
) : Parcelable

@Parcelize
data class TransitDetails(
    val stopDetails: StopDetails,
    val localizedValues: LocalizedValues,
    val headsign: String,
    val transitLine: TransitLine,
    val stopCount: Long,
) : Parcelable

@Parcelize
data class StopDetails(
    val arrivalStop: ArrivalStop,
    val arrivalTime: String,
    val departureStop: DepartureStop,
    val departureTime: String,
) : Parcelable

@Parcelize
data class ArrivalStop(
    val name: String,
    val location: Location,
) : Parcelable

@Parcelize
data class Location(
    val latLng: LatLng,
) : Parcelable

@Parcelize
data class LatLng(
    val latitude: Double,
    val longitude: Double,
) : Parcelable

@Parcelize
data class DepartureStop(
    val name: String,
    val location: Location2,
) : Parcelable

@Parcelize
data class Location2(
    val latLng: LatLng2,
) : Parcelable

@Parcelize
data class LatLng2(
    val latitude: Double,
    val longitude: Double,
) : Parcelable

@Parcelize
data class LocalizedValues(
    val arrivalTime: ArrivalTime,
    val departureTime: DepartureTime,
) : Parcelable

@Parcelize
data class ArrivalTime(
    val time: Time,
    val timeZone: String,
) : Parcelable

@Parcelize
data class Time(
    val text: String,
) : Parcelable

@Parcelize
data class DepartureTime(
    val time: Time2,
    val timeZone: String,
) : Parcelable

@Parcelize
data class Time2(
    val text: String,
) : Parcelable

@Parcelize
data class TransitLine(
    val agencies: List<Agency>,
    val name: String,
    val color: String,
    val nameShort: String?,
    val textColor: String,
    val vehicle: Vehicle,
) : Parcelable

@Parcelize
data class Agency(
    val name: String,
    val phoneNumber: String,
    val uri: String,
) : Parcelable

@Parcelize
data class Vehicle(
    val name: Name,
    val type: String,
    val iconUri: String,
) : Parcelable

@Parcelize
data class Name(
    val text: String,
) : Parcelable