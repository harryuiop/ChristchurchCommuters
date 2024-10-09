package com.example.busapp.models

class UserData(
    val id: Int,
    val selectedStop: BusStop
): Identifiable {
    override fun getIdentifier(): Int {
        return id;
    }
}