//package com.example.busapp.viewmodels
//
//import android.util.Log
//import com.example.busapp.datastore.Storage
//import com.example.busapp.models.BusStop
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.flow.collect
//
//
//class BusStopViewModel(
//    private val busStopStorage: Storage<BusStop>
//): ViewModel() {
//    private val _busStops = MutableStateFlow<List<BusStop>>(emptyList())
//    val busStops: StateFlow<List<BusStop>> = _busStops.asStateFlow()
//
//    private val _selectedBusStop = MutableStateFlow<BusStop?>(null)
//    val selectedBusStop: StateFlow<BusStop?> = _selectedBusStop
//
//    fun getAllBusStops() = viewModelScope.launch {
//        busStopStorage.getAll().catch { Log.e("BUS_STOP_VIEW_MODEL", it.toString()) }
//                .collect{_busStops.emit(it)}
//        }
//
//    fun getBusStopById(stopId: Int?) = viewModelScope.launch {
//        if (stopId != null) {
//            _selectedBusStop.value = busStopStorage.get { it.getIdentifier() == stopId }.first()
//        } else {
//            _selectedBusStop.value = null
//        }
//    }
//
//    fun addBusStop(busStop: BusStop) = viewModelScope.launch {
//        busStopStorage.insert(busStop).catch { e ->
//            Log.e("BUS_STOP_VIEW_MODEL", "Error inserting bus stop: ${e.message}", e) }
//            .collect()
//        busStopStorage.getAll().catch { Log.e("BUS_STOP_VIEW_MODEL", it.toString()) }
//            .collect { _busStops.emit(it) }
//    }
//}
//
