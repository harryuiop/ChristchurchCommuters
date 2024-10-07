package com.example.busapp.models

data class TransitRouteRequest(
    val origin: Origin,
    val destination: Destination,
    val travelMode: String,
    val computeAlternativeRoutes: Boolean,
    val transitPreferences: TransitPreferences,
)

data class Origin(
    val address: String,
)

data class Destination(
    val address: String,
)

data class TransitPreferences(
    val routingPreference: String,
    val allowedTravelModes: List<String>,
)