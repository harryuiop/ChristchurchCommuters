package com.example.busapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.busapp.models.FileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimetableViewModel() : ViewModel() {
    private val _routes = MutableStateFlow<List<List<String?>>>(emptyList())
    val routes: StateFlow<List<List<String?>>> get() = _routes

    private val _trips = MutableStateFlow<List<List<String?>>>(emptyList())
    val trips: StateFlow<List<List<String?>>> get() = _trips

    private val _stopTimes = MutableStateFlow<List<List<String?>>>(emptyList())
    val stopTimes: StateFlow<List<List<String?>>> get() = _stopTimes

    private val _stops = MutableStateFlow<List<List<String?>>>(emptyList())
    val stops: StateFlow<List<List<String?>>> get() = _stops

    fun setData(fileData: FileData) {
        _routes.value = fileData.routes
        _trips.value = fileData.trips
        _stopTimes.value = fileData.stopTimes
        _stops.value = fileData.stops
    }
}