package com.example.busapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RouteFinderViewmodel: ViewModel() {
    var startLocation by mutableStateOf("")
        private  set

    var destination by mutableStateOf("")
        private set

    fun updateStartLocation(newStartLocation : String) {
        startLocation = newStartLocation
    }

    fun updateDestination(newDestination : String) {
        destination = newDestination
    }
}