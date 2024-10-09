package com.example.busapp.models
import java.util.Date

data class LiveBusViaStop (
    val tripId: String,
    val stopId: String,
    val scheduleRelationship: String,
    val arrivalDelay: Int,
    val departureDelay: Int,
    val arrivalTime: Date,
    val departureTime: Date
)