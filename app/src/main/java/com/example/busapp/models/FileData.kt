package com.example.busapp.models

data class FileData(
    val routes: List<List<String>>,
    val sundayTripsPerRouteDirection0: Map<String, MutableList<String>>,
    val fridayTripsPerRouteDirection0: Map<String, MutableList<String>>,
    val mondayToFridayTripsPerRouteDirection0: Map<String, MutableList<String>>,
    val saturdayTripsPerRouteDirection0: Map<String, MutableList<String>>,
    val sundayTripsPerRouteDirection1: Map<String, MutableList<String>>,
    val fridayTripsPerRouteDirection1: Map<String, MutableList<String>>,
    val mondayToFridayTripsPerRouteDirection1: Map<String, MutableList<String>>,
    val saturdayTripsPerRouteDirection1: Map<String, MutableList<String>>,
    val tripIdToHeadboard: Map<String, String>,
    val tripIdToRouteId: Map<String, String>,
    val tripIdToNameNumber: Map<String, Pair<String, String>>,
    val stopTimesPerTrip: Map<String, MutableList<Pair<String, String>>>,
    val stopNamesPerTrip: Map<String, MutableList<String>>
)