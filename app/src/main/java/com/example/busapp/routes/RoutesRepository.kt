package com.example.busapp.routes

import com.example.busapp.models.TransitRoutesResponse

interface RoutesRepository {
    fun getRoutes(startLocation: String, destination: String, departureTime: String): TransitRoutesResponse

    fun parseResponse(response: String): TransitRoutesResponse
}