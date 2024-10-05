package com.example.busapp.services

import com.example.busapp.models.*
import com.google.transit.realtime.GtfsRealtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MetroApiService {

    /**
     * Calls the API asynchronously within a coroutine and passes off to parseGtfsRealtimeFeed() to
     * create the objects
     */
    suspend fun getRealTimeData(): GtfsRealtimeFeed = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val key = com.example.busapp.BuildConfig.METRO_API_KEY
            val urlString = com.example.busapp.BuildConfig.METRO_API_URL
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", key)
            connection.requestMethod = "GET"

            println("Attempting to connect to $urlString")
            val status = connection.responseCode
            println("Response Code: $status")

            if (status == HttpURLConnection.HTTP_OK) {
                val feed = GtfsRealtime.FeedMessage.parseFrom(connection.inputStream)
                parseGtfsRealtimeFeed(feed)
            } else {
                throw IOException("HTTP error code: $status")
            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * This function is AI generated
     * Takes the output from requesting the Metro API live updates and parses them into objects
     *
     * @param feed: GtfsRealtime.FeedMessage - Googles GftsRealtime Feed Message
     */
    private fun parseGtfsRealtimeFeed(feed: GtfsRealtime.FeedMessage): GtfsRealtimeFeed {
        val lastUpdated = Date(feed.header.timestamp * 1000)
        val tripUpdates = feed.entityList
            .filter { it.hasTripUpdate() }
            .map { entity ->
                val tripUpdate = entity.tripUpdate
                TripUpdate(
                    tripId = tripUpdate.trip.tripId,
                    routeId = tripUpdate.trip.routeId,
                    scheduleRelationship = tripUpdate.trip.scheduleRelationship.name,
                    stopTimeUpdates = tripUpdate.stopTimeUpdateList.map { stu ->
                        StopTimeUpdate(
                            stopSequence = stu.stopSequence,
                            stopId = stu.stopId,
                            arrival = if (stu.hasArrival()) createStopTimeEvent(stu.arrival) else null,
                            departure = if (stu.hasDeparture()) createStopTimeEvent(stu.departure) else null
                        )
                    }
                )
            }

        return GtfsRealtimeFeed(lastUpdated, tripUpdates)
    }

    /**
     * Helper function to create StopTimeEvent objects
     */
    private fun createStopTimeEvent(event: GtfsRealtime.TripUpdate.StopTimeEvent): StopTimeEvent {
        return StopTimeEvent(
            delay = event.delay,
            time = Date(event.time * 1000)
        )
    }
}