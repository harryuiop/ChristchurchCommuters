package com.example.busapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.busapp.models.GtfsRealtimeFeed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class GtfsRealTimeViewModel: ViewModel() {
    private val _feed = MutableStateFlow<GtfsRealtimeFeed>(GtfsRealtimeFeed(
                                                            lastUpdated = Date(0),
                                                            tripUpdates = emptyList()))
    val feed: StateFlow<GtfsRealtimeFeed> get() = _feed

    fun setData(feedData: GtfsRealtimeFeed) {
        _feed.value = feedData
    }
}