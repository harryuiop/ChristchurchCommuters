package com.example.busapp.models

data class BusStop (
    val id: Int,
): Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }
}