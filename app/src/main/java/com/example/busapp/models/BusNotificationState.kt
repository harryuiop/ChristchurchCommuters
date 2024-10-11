package com.example.busapp.models

data class BusNotificationState(
    var isNotificationEnabled: Boolean = false,
    var hasFiveMinNotified: Boolean = false,
    var hasOneMinNotified: Boolean = false
)