package com.example.busapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.busapp.models.FileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimetableViewModel() : ViewModel() {
    private val _routes = MutableStateFlow<List<List<String>>>(emptyList())
    val routes: StateFlow<List<List<String>>> get() = _routes

    private val _sundayTripsPerRouteDirection0 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val sundayTripsPerRouteDirection0: StateFlow<Map<String, MutableList<String>>> get() = _sundayTripsPerRouteDirection0

    private val _fridayTripsPerRouteDirection0 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val fridayTripsPerRouteDirection0: StateFlow<Map<String, MutableList<String>>> get() = _fridayTripsPerRouteDirection0

    private val _mondayToFridayTripsPerRouteDirection0 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val mondayToFridayTripsPerRouteDirection0: StateFlow<Map<String, MutableList<String>>> get() = _mondayToFridayTripsPerRouteDirection0

    private val _saturdayTripsPerRouteDirection0 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val saturdayTripsPerRouteDirection0: StateFlow<Map<String, MutableList<String>>> get() = _saturdayTripsPerRouteDirection0

    private val _sundayTripsPerRouteDirection1 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val sundayTripsPerRouteDirection1: StateFlow<Map<String, MutableList<String>>> get() = _sundayTripsPerRouteDirection1

    private val _fridayTripsPerRouteDirection1 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val fridayTripsPerRouteDirection1: StateFlow<Map<String, MutableList<String>>> get() = _fridayTripsPerRouteDirection1

    private val _mondayToFridayTripsPerRouteDirection1 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val mondayToFridayTripsPerRouteDirection1: StateFlow<Map<String, MutableList<String>>> get() = _mondayToFridayTripsPerRouteDirection1

    private val _saturdayTripsPerRouteDirection1 = MutableStateFlow<Map<String, MutableList<String>>>(emptyMap())
    val saturdayTripsPerRouteDirection1: StateFlow<Map<String, MutableList<String>>> get() = _saturdayTripsPerRouteDirection1

    private val _stopTimesPerTrip = MutableStateFlow<Map<String, MutableList<Pair<String, String>>>>(
        emptyMap())
    val stopTimesPerTrip: StateFlow<Map<String, MutableList<Pair<String, String>>>> get() = _stopTimesPerTrip

    private val _stopNamesPerTrip = MutableStateFlow<Map<String, MutableList<String>>>(
        emptyMap())
    val stopNamesPerTrip: StateFlow<Map<String, MutableList<String>>> get() = _stopNamesPerTrip

    fun setData(fileData: FileData) {
        _routes.value = fileData.routes
        _sundayTripsPerRouteDirection0.value = fileData.sundayTripsPerRouteDirection0
        _fridayTripsPerRouteDirection0.value = fileData.fridayTripsPerRouteDirection0
        _mondayToFridayTripsPerRouteDirection0.value = fileData.mondayToFridayTripsPerRouteDirection0
        _saturdayTripsPerRouteDirection0.value = fileData.saturdayTripsPerRouteDirection0
        _sundayTripsPerRouteDirection1.value = fileData.sundayTripsPerRouteDirection1
        _fridayTripsPerRouteDirection1.value = fileData.fridayTripsPerRouteDirection1
        _mondayToFridayTripsPerRouteDirection1.value = fileData.mondayToFridayTripsPerRouteDirection1
        _saturdayTripsPerRouteDirection1.value = fileData.saturdayTripsPerRouteDirection1
        _stopTimesPerTrip.value = fileData.stopTimesPerTrip
        _stopNamesPerTrip.value = fileData.stopNamesPerTrip
    }
}