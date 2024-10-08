package com.example.busapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.busapp.models.BusStop

class AddBusStopViewModel: ViewModel() {
    var userQuery by mutableStateOf("")
        private set

    fun updateQuery(query: String) {
        userQuery = query
    }

    var busStops = mutableStateListOf<List<BusStop>>()
        private set

    fun setBusStops(recvBusStops: List<BusStop>) {
        busStops.add(recvBusStops)
    }

    fun getBusStops(): List<BusStop> {
        return busStops.last()
    }


    var filteredBusStops by mutableStateOf(listOf<BusStop>())
        private set

//    fun updateFilteredBusStops() {
//        filteredBusStops = busStops
//    }

    var selectedBusStop by mutableStateOf(-1)
        private set

    fun updateSelectedBusStop(index: Int) {
        selectedBusStop = index
    }



}