package com.example.busapp.models

data class FileData(
    val routes: List<List<String?>>,
    val tripsPerRoute: HashMap<String?, MutableList<String?>>,
    val stopTimesPerTrip: HashMap<String?, MutableList<Pair<String?, String?>>>,
    val stopNamesPerTrip: HashMap<String?, MutableList<String?>>
)