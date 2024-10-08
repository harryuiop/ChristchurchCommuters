package com.example.busapp.services

import android.content.Context
import android.util.Log
import com.example.busapp.R
import com.example.busapp.models.FileData
import kotlinx.coroutines.coroutineScope
import java.io.BufferedReader
import java.io.InputStreamReader

suspend fun readMetroFiles(context: Context) = coroutineScope {
    val routes = mutableListOf<List<String>>()
    val stopTimesPerTrip = HashMap<String, MutableList<Pair<String, String>>>()
    val stopsHashMap = HashMap<String, String>()

    val sundayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()
    val fridayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()
    val mondayToFridayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()
    val saturdayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()

    val tripIdToHeadboard = HashMap<String, String>()
    val tripIdToRouteID = HashMap<String, String>()
    val tripIdToNameNumber =  HashMap<String, Pair<String, String>>()

    val sundayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()
    val fridayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()
    val mondayToFridayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()
    val saturdayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()

    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.routes)))
        var line: String?
        var counter = 0
        while (reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                //Adding route_id, route_short_name, route_long_name
                routes.add(listOf(listLine[0], listLine[2], listLine[3]))
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.trips)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                val direction = listLine[5]
                if (direction == "0") {
                    when (listLine[1]) {
                        "1" -> addToMap(sundayTripsPerRouteDirection0, listLine)
                        "2" -> addToMap(fridayTripsPerRouteDirection0, listLine)
                        "3" -> addToMap(mondayToFridayTripsPerRouteDirection0, listLine)
                        "4" -> addToMap(saturdayTripsPerRouteDirection0, listLine)
                    }
                } else {
                    when (listLine[1]) {
                        "1" -> addToMap(sundayTripsPerRouteDirection1, listLine)
                        "2" -> addToMap(fridayTripsPerRouteDirection1, listLine)
                        "3" -> addToMap(mondayToFridayTripsPerRouteDirection1, listLine)
                        "4" -> addToMap(saturdayTripsPerRouteDirection1, listLine)
                    }
                }
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.stop_times)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                //Only stop times with timepoint = 1 are displayed on metro website
                if (listLine[9] == "1") {
                    val tripId = listLine[0]
                    if (!stopTimesPerTrip.containsKey(tripId)) {
                        //Pair of arrival_time and stop_id
                        stopTimesPerTrip[tripId] = mutableListOf(Pair(listLine[1], listLine[3]))
                    } else {
                        val currentList = stopTimesPerTrip[tripId]
                        currentList?.add(Pair(listLine[1], listLine[3]))
                        if (currentList != null) {
                            stopTimesPerTrip[tripId] = currentList
                        }
                    }
                }
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.stops)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                //Adding stop_id, stop_name
                stopsHashMap[listLine[0]] = listLine[2]
            }
            counter++
        }
    } catch (e: Exception) {
        Log.e("Reading Metro Files","Error: ${e.message}")
    } finally {
        try {
            reader?.close()
        } catch (e: Exception) {
            Log.e("Reading Metro Files","Error: ${e.message}")
        }
    }
    val stopNamesPerTrip = HashMap<String, MutableList<String>>()
    stopTimesPerTrip.forEach { (key, value) ->
        val stopNamesList = mutableListOf<String>()
        value.forEach { stopNamePair ->
            stopsHashMap[stopNamePair.second]?.let { stopNamesList.add(it) }
        }
        stopNamesPerTrip[key] = stopNamesList
    }

    return@coroutineScope FileData(
        routes,
        sundayTripsPerRouteDirection0,
        fridayTripsPerRouteDirection0,
        mondayToFridayTripsPerRouteDirection0,
        saturdayTripsPerRouteDirection0,
        sundayTripsPerRouteDirection1,
        fridayTripsPerRouteDirection1,
        mondayToFridayTripsPerRouteDirection1,
        saturdayTripsPerRouteDirection1,
        tripIdToHeadboard,
        tripIdToRouteId,
        tripIdToNameNumber,
        stopTimesPerTrip,
        stopNamesPerTrip)
}

fun addToMap(
    toAdd: HashMap<String, MutableList<String>>,
    listLine: List<String>,
) {
    val routeId = listLine[0]
    if (!toAdd.containsKey(routeId)) {
        //Adding trip_id
        toAdd[routeId] = mutableListOf(listLine[2])
    } else {
        val currentList = toAdd[routeId]
        currentList?.add(listLine[2])
        if (currentList != null) {
            toAdd[routeId] = currentList
        }
    }
}