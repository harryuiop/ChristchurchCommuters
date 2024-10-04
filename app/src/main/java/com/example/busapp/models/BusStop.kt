package com.example.busapp.models

class BusStop (
    val id: Int,
    val stopId: Int,
    val stopCode: Int,
    val stopName: String,
    val stop_desc: String?,
    val stop_lat: Double,
    val stop_lon: Double,
    //zone_id
    //stop_url
    //location_type
    //parent_station
    //stop_timezone
    val wheelchair_boarding: Boolean

): Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }

    companion object {
        fun getBusStops(): List<BusStop> {
            return listOf(
                BusStop(
                    1,
                    1,
                    1,
                    "Ilam",
                    null,
                    192.000,
                    192.000,
                    false
                ),
                BusStop(
                    2,
                    2,
                    3,
                    "Sumner",
                    null,
                    192.000,
                    192.000,
                    false
                ),
                BusStop(
                    3,
                    3,
                    5,
                    "Riccarton",
                    null,
                    172.777,
                    43.531,
                    false
                ),
                BusStop(
                    4,
                    4,
                    7,
                    "Burnside",
                    null,
                    172.740,
                    43.544,
                    false
                ),
                BusStop(
                    5,
                    1,
                    9,
                    "City Centre",
                    null,
                    172.633,
                    43.531,
                    false
                ),
                BusStop(
                    6,
                    2,
                    11,
                    "Lyttelton",
                    null,
                    172.719,
                    43.603,
                    false
                ),
                BusStop(
                    7,
                    3,
                    13, "New Brighton",
                    null,
                    172.738,
                    43.489,
                    false
                ),
                BusStop(
                    8,
                    4,
                    15,
                    "Airport",
                    null,
                    172.532,
                    43.489,
                    false
                )
            )
        }
    }
}