package com.example.busapp.routes

import android.util.Log
import com.example.busapp.BuildConfig
import com.example.busapp.models.Destination
import com.example.busapp.models.Origin
import com.example.busapp.models.TransitPreferences
import com.example.busapp.models.TransitRouteRequest
import com.example.busapp.models.TransitRoutesResponse
import com.google.gson.Gson
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class RoutesRepositoryImpl: RoutesRepository {
    private val gson = Gson()

    override fun getRoutes(
        startLocation: String,
        destination: String,
        departureTime: String
    ): TransitRoutesResponse {
        val apiKey = BuildConfig.MAPS_API_KEY
        val urlString = BuildConfig.ROUTES_API_URL
        val url = URL(urlString)
        var connection: HttpURLConnection? = null

        val transitRouteRequest = TransitRouteRequest(
            origin = Origin(startLocation),
            destination = Destination(destination),
            travelMode = "TRANSIT",
            departureTime = departureTime,
            computeAlternativeRoutes = true,
            transitPreferences = TransitPreferences(
                routingPreference = "LESS_WALKING",
                allowedTravelModes = listOf("BUS")
            )
        )

        val jsonBody = gson.toJson(transitRouteRequest)

        return try {
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-Goog-Api-Key", apiKey)
            connection.setRequestProperty("X-Goog-FieldMask", "routes.legs.steps.transitDetails")
            connection.requestMethod = "POST"
            connection.doOutput = true

            val outputStream: OutputStream = connection.outputStream
            outputStream.write(jsonBody.toByteArray())
            outputStream.flush()
            outputStream.close()

            val status = connection.responseCode
            Log.i("RoutesAPI", "Response code: $status")

            if (status == HttpURLConnection.HTTP_OK) {
                println(transitRouteRequest)
                connection.inputStream.bufferedReader().use {
                    parseResponse(it.readText())
                }
            } else {
                Log.e("RoutesAPI", "HTTP error code: $status")
                TransitRoutesResponse(emptyList())
            }

        } catch (e: Exception) {
            Log.e("RoutesAPI", "Error: ${e.message}")
            TransitRoutesResponse(emptyList())
        } finally {
            connection?.disconnect()
        }
    }

    override fun parseResponse(response: String): TransitRoutesResponse {
        return gson.fromJson(response, TransitRoutesResponse::class.java)
    }
}