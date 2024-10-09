package com.example.busapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.busapp.models.BusStop
import kotlinx.coroutines.flow.MutableStateFlow

class AddBusStopViewModel: ViewModel() {
    var userQuery by mutableStateOf("")
        private set

    fun updateQuery(query: String) {
        userQuery = query
    }

    private var _busStops = MutableStateFlow<Map<String, String>>(
        emptyMap())
    val busStops: MutableStateFlow<Map<String, String>> get() = _busStops


    fun addBusStops(addedBusStops: Map<String, String>) {
        _busStops.value = addedBusStops
    }




    var filteredBusStops by mutableStateOf(listOf<BusStop>())
        private set

//    fun updateFilteredBusStops() {
//        filteredBusStops = busStops
//    }

    var selectedBusStop by mutableStateOf(BusStop(-1, ""))
        private set

    fun updateSelectedBusStop(id: Int, name: String) {
        selectedBusStop = BusStop(id, name)
    }



}