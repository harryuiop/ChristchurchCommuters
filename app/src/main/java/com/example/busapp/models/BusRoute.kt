package com.example.busapp.models

class BusRoute (
    val id: Int,
    val name: String,
): Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }
}