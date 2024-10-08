package com.example.busapp.models

class UserData(
    val id: Int,
    val selectedStopId: Int
): Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }

}