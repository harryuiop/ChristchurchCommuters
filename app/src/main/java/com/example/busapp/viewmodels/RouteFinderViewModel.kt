package com.example.busapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.busapp.places.PlacesRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction

class RouteFinderViewModel(
    private val placesRepository: PlacesRepository
) : ViewModel() {
    var startLocation by mutableStateOf("")
        private set

    var destination by mutableStateOf("")
        private set

    fun updateStartLocation(newStartLocation : String) {
        startLocation = newStartLocation
    }

    fun updateDestination(newDestination : String) {
        destination = newDestination
    }

    fun findAutocompletePredictions(newQuery: String, onResult: (List<AutocompletePrediction>) -> Unit) {
        placesRepository.findAutocompletePredictions(newQuery, onResult)
    }
}