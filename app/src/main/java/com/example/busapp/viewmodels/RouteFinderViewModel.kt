package com.example.busapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.busapp.places.PlacesRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import java.util.Calendar

class RouteFinderViewModel(
    private val placesRepository: PlacesRepository
) : ViewModel() {
    var startLocation by mutableStateOf("")
        private set

    var destination by mutableStateOf("")
        private set

    var travelTimeOption by mutableStateOf("Arrive by")
        private set

    var calendar: Calendar by mutableStateOf(Calendar.getInstance())
        private set

    fun updateStartLocation(newStartLocation : String) {
        startLocation = newStartLocation
    }

    fun updateDestination(newDestination : String) {
        destination = newDestination
    }

    fun findAutocompletePredictions(newQuery: String, onResult: (List<AutocompletePrediction>) -> Unit) {
        if (newQuery.length > 2) {
            placesRepository.findAutocompletePredictions(newQuery, onResult)
        } else {
            onResult(emptyList())
        }
    }

    fun updateTravelTimeOption(newTravelTimeOption : String) {
        travelTimeOption = newTravelTimeOption
    }

    fun updateCalendar(newCalendar: Calendar) {
        calendar = newCalendar
    }

    fun resetValues() {
        startLocation = ""
        destination = ""
        calendar = Calendar.getInstance()
    }
}