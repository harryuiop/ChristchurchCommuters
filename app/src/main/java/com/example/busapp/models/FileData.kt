package com.example.busapp.models

data class FileData(
    val routes: List<List<String?>>,
    val trips: List<List<String?>>,
    val stopTimes: List<List<String?>>,
    val stops: List<List<String?>>
)