package com.example.busapp.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class RouteFinderViewmodel(
    private val placesClient : PlacesClient
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

    fun autoCompleteRequest(query : String) {
        val placeFields = getPlaceFields()
        val center = LatLng(37.7749, -122.4194)
        val circle = CircularBounds.newInstance(center, 5000.0) // radius in meters
        val autoCompletePlacesRequest = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setRegionCode("NZ")
            .setLocationRestriction(circle)
            .build()

        placesClient.findAutocompletePredictions(autoCompletePlacesRequest)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions// Handle the predictions here
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "some exception happened ${exception.message}")
            }
    }

    fun getPlaceFields(): List<Field> {
        return listOf(Field.NAME, Field.ADDRESS)
    }
}