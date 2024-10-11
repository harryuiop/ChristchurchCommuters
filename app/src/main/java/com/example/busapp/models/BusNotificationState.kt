package com.example.busapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusNotificationState(
    var isNotificationEnabled: Boolean = false,
    var hasFiveMinNotified: Boolean = false,
    var hasOneMinNotified: Boolean = false
) : Parcelable