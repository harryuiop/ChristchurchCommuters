package com.example.busapp.places

interface PlacesRepository {
    fun findAutocompletePredictions(query: String)
}