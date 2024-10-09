package com.example.busapp.models

data class StopTime(
    val id: Int,
    val tripId: String,
    val stopId: String,
    val arrivalTime: String,
    val departureTime: String
): Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }
}