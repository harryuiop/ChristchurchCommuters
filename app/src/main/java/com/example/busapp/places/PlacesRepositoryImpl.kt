package com.example.busapp.places

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class PlacesRepositoryImpl(
    private val placesClient: PlacesClient
) : PlacesRepository {
    override fun findAutocompletePredictions(query: String, onResult: (List<AutocompletePrediction>) -> Unit) {
        val center = LatLng(-43.531111, 172.636111)
        val circle = CircularBounds.newInstance(center, 30000.0) // radius in meters
        val autoCompletePlacesRequest = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setRegionCode("NZ")
            .setLocationRestriction(circle)
            .build()

        placesClient.findAutocompletePredictions(autoCompletePlacesRequest)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions// Handle the predictions here
                onResult(predictions)
            }
            .addOnFailureListener { exception ->
                Log.e("PlacesRepository", "some exception happened ${exception.message}")
                onResult(emptyList())
            }
    }
}