package com.example.busapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.busapp.models.FileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimetableViewModel() : ViewModel() {
    private val _routes = MutableStateFlow<List<List<String>>>(emptyList())
    val routes: StateFlow<List<List<String>>> get() = _routes

    private val _tripsPerRoute = MutableStateFlow<Map<String, MutableList<Pair<String, String>>>>(emptyMap())
    val tripsPerRoute: StateFlow<Map<String, MutableList<Pair<String, String>>>> get() = _tripsPerRoute

    private val _stopTimesPerTrip = MutableStateFlow<Map<String, MutableList<Pair<String, String>>>>(
        emptyMap())
    val stopTimesPerTrip: StateFlow<Map<String, MutableList<Pair<String, String>>>> get() = _stopTimesPerTrip

    private val _stopNamesPerTrip = MutableStateFlow<Map<String, MutableList<String>>>(
        emptyMap())
    val stopNamesPerTrip: StateFlow<Map<String, MutableList<String>>> get() = _stopNamesPerTrip

    fun setData(fileData: FileData) {
        _routes.value = fileData.routes
        _tripsPerRoute.value = fileData.tripsPerRoute
        _stopTimesPerTrip.value = fileData.stopTimesPerTrip
        _stopNamesPerTrip.value = fileData.stopNamesPerTrip
    }
}