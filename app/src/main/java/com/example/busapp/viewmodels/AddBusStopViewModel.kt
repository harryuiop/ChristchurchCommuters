package com.example.busapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AddBusStopViewModel: ViewModel() {
    var userQuery by mutableStateOf("")
        private set

    fun updateQuery(query: String) {
        userQuery = query
    }
}