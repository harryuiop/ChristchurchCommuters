package com.example.busapp.models

data class FileData(
    val routes: List<List<String?>>,
    val tripsPerRoute: Map<String?, MutableList<String?>>,
    val stopTimesPerTrip: Map<String?, MutableList<Pair<String?, String?>>>,
    val stopNamesPerTrip: Map<String?, MutableList<String?>>
)