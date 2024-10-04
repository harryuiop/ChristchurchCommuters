package com.example.busapp.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MetroApiService {

    suspend fun getRealTimeData(key: String): String = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val urlString = "https://apis.metroinfo.co.nz/rti/gtfsrt/v1/trip-updates.pb"
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            // Set a timeout for the connection
            connection.connectTimeout = 10000 // 10 seconds
            connection.readTimeout = 10000 // 10 seconds

            // Request headers
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", key)
            connection.requestMethod = "GET"

            println("Attempting to connect to $urlString")
            val status = connection.responseCode
            println("Response Code: $status")

            if (status == HttpURLConnection.HTTP_OK) {
                // Read the input stream as bytes and convert to a hex string
                connection.inputStream.use { input ->
                    input.ProtoBuf.decodeFromByteArray()
                }
            } else {
                "Error: Response Code $status\nError stream: ${connection.errorStream?.bufferedReader()?.readText() ?: "No error stream"}"
            }
        } catch (e: Exception) {
            "Error occurred: ${e.message ?: "No error message"}\nError type: ${e.javaClass.simpleName}"
        } finally {
            connection?.disconnect()
        }
    }
}