package com.example.busapp.places

import com.google.android.libraries.places.api.model.AutocompletePrediction

interface PlacesRepository {
    fun findAutocompletePredictions(query: String, onResult: (List<AutocompletePrediction>) -> Unit)
}