package com.example.busapp.models

import java.util.Date

/**
 * The documentation is from google's website
 */

/**
 *  GtfsRealtimeFeed
 *      lastUpdated: Date: The timestamp when the feed was last updated. This helps you know how recent the information is.
 *      tripUpdates: List<TripUpdate>: A list of all trip updates in the feed.
 */
data class GtfsRealtimeFeed(
    val lastUpdated: Date,
    val tripUpdates: List<TripUpdate>
)

/**
 *  TripUpdate
 *      tripId: String: A unique identifier for the trip. This corresponds to the trip_id in the GTFS static data.
 *      routeId: String: The route identifier for this trip. This corresponds to the route_id in the GTFS static data.
 *      scheduleRelationship: String: Indicates how this trip relates to the scheduled trips. Common values are:
 *
 *      "SCHEDULED": The trip is running according to schedule.
 *      "ADDED": An extra trip that was added in addition to a running schedule.
 *      "CANCELED": A trip that has been canceled and is not running.
 *
 *      stopTimeUpdates: List<StopTimeUpdate>: A list of updates to the scheduled stops for this trip.
 */
data class TripUpdate(
    val tripId: String,
    val routeId: String,
    val scheduleRelationship: String,
    val stopTimeUpdates: List<StopTimeUpdate>
)

/**
 *  StopTimeUpdate
 *      stopSequence: Int: The sequence of the stop within the trip. This helps to order the stops.
 *      stopId: String: The unique identifier for the stop. This corresponds to the stop_id in the GTFS static data.
 *      arrival: StopTimeEvent?: Information about the arrival at this stop, if available.
 *      departure: StopTimeEvent?: Information about the departure from this stop, if available.
 */
data class StopTimeUpdate(
    val stopSequence: Int,
    val stopId: String,
    val arrival: StopTimeEvent?,
    val departure: StopTimeEvent?
)

/**
 *  StopTimeEvent
 *      delay: Int: The delay in seconds from the scheduled time. Positive values indicate the transit vehicle is running late, negative values indicate it's running early.
 *      time: Date: The estimated time of arrival or departure.
 */
data class StopTimeEvent(
    val delay: Int,
    val time: Date
)
