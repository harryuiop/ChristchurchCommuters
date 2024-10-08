package com.example.busapp.models

class BusStop (
    val id: Int,
    val stopName: String,


): Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }

}