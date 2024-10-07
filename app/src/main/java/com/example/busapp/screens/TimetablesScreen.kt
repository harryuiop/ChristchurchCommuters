package com.example.busapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import com.example.busapp.viewmodels.TimetableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ViewTimetables(
    navController: NavController,
    timetableViewModel: TimetableViewModel
) {
    val routes: List<List<String>> by timetableViewModel.routes.collectAsState()
    //Trips have pair of trip_id and service_id, service_id = 1,2,3,4.
    // - service_id: 1 = Sunday, 2 = Friday, 3 = Monday-Friday, 4 = Saturday
    val sundayTripsPerRouteDirection0: Map<String, MutableList<String>> by timetableViewModel.sundayTripsPerRouteDirection0.collectAsState()
    val fridayTripsPerRouteDirection0: Map<String, MutableList<String>> by timetableViewModel.fridayTripsPerRouteDirection0.collectAsState()
    val mondayToFridayTripsPerRouteDirection0: Map<String, MutableList<String>> by timetableViewModel.mondayToFridayTripsPerRouteDirection0.collectAsState()
    val saturdayTripsPerRouteDirection0: Map<String, MutableList<String>> by timetableViewModel.saturdayTripsPerRouteDirection0.collectAsState()

    val sundayTripsPerRouteDirection1: Map<String, MutableList<String>> by timetableViewModel.sundayTripsPerRouteDirection1.collectAsState()
    val fridayTripsPerRouteDirection1: Map<String, MutableList<String>> by timetableViewModel.fridayTripsPerRouteDirection1.collectAsState()
    val mondayToFridayTripsPerRouteDirection1: Map<String, MutableList<String>> by timetableViewModel.mondayToFridayTripsPerRouteDirection1.collectAsState()
    val saturdayTripsPerRouteDirection1: Map<String, MutableList<String>> by timetableViewModel.saturdayTripsPerRouteDirection1.collectAsState()

    val stopTimesPerTrip: Map<String, MutableList<Pair<String, String>>> by timetableViewModel.stopTimesPerTrip.collectAsState()
    val stopNamesPerTrip: Map<String, MutableList<String>> by timetableViewModel.stopNamesPerTrip.collectAsState()
    println("timetable screen")
    println(mondayToFridayTripsPerRouteDirection1["100_36_3"])
    //see what stop names get printed
    mondayToFridayTripsPerRouteDirection1["100_36_3"]?.forEach { i ->
        println(stopNamesPerTrip[i])
    }
    println(stopNamesPerTrip[mondayToFridayTripsPerRouteDirection1["100_36_3"]?.get(1)])
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedRouteName by rememberSaveable { mutableStateOf("")}
    var selectedRouteId by rememberSaveable { mutableStateOf("") }
    var selectedDay by rememberSaveable { mutableStateOf("") }
    var zeroDirection by rememberSaveable { mutableStateOf(false) }
    var headerList by rememberSaveable { mutableStateOf(mutableListOf<String>()) }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Timetables", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.size(20.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        value = selectedRouteName,
                        placeholder = { Text("Select Bus Service") },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        routes.forEach { route ->
                            DropdownMenuItem(
                                text = { Text(text = route[1] + " - " + route[2]) },
                                onClick = {
                                    selectedRouteName = route[1] + " - " + route[2]
                                    selectedRouteId = route[0]

                                    selectedDay = "3"
                                    zeroDirection = false

                                    headerList = findMostNames(mondayToFridayTripsPerRouteDirection0[route[0]]!!, stopNamesPerTrip)
                                    expanded = false
                                })
                        }
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Button(
                    onClick = {
                        zeroDirection = !zeroDirection
                        if (!zeroDirection) {
                            when (selectedDay) {
                                "1" -> headerList = findMostNames(sundayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                "2" -> headerList = findMostNames(fridayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                "3" -> headerList = findMostNames(mondayToFridayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                "4" -> headerList = findMostNames(saturdayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                            }
                        } else {
                            when (selectedDay) {
                                "1" -> headerList = findMostNames(sundayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                "2" -> headerList = findMostNames(fridayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                "3" -> headerList = findMostNames(mondayToFridayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                "4" -> headerList = findMostNames(saturdayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                            }
                        }
                    }
                ) {
                    Text("View Other Direction", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = { selectedDay = "3" }
                    ) {
                        Text("Weekday")
                    }
                    Button(
                        onClick = { selectedDay = "4" }
                    ) {
                        Text("Saturday")
                    }
                    Button(
                        onClick = { selectedDay = "1" }
                    ) {
                        Text("Sunday")
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

            }
        }

        //From routes.txt: Get route_id, route_short_name, route_long_name
        //  - display route_short_name/route_long_name at the top of page
        //From trips.csv: Get trip_id. direction_id
        // - use direction_id to know which direction bus is, use in View Other Direction button
        //From stop_times.csv: For trip_id get all stop_id's, arrival_time/departure_time
        //  - display the arrival_time/departure_time in the table
        //From stops.txt: For stop_id get stop_name
        // - display the stop_name as the column headers

        //TODO: Get the data from the stop_times/trips/stops files
        //val headerList = stopNamesPerRoute[selectedRouteId]
        val headerList2 = listOf("Eastgate Mall (Buckleys Rd)", "St Martins Shops", "Princess Margaret Hospital", "Barrington Mall (Barrington St)", "Westfield Riccarton", "Burnside High School", "Northlands Platform B", "The Palms(North Parade)", "Eastgate Mall (Buckleys Road)")
        val dataList = listOf("12:30pm","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a")

        val columnHeaderModifier = Modifier
            .border(1.dp, Color.Black)
            .wrapContentSize()
            .padding(4.dp)
        val dataModifier = Modifier
            .border(1.dp, Color.Black)
            .wrapContentSize()
            .padding(4.dp)

        //TODO: Change value in here to num stops in route
        val numColumns = 9
        item {
            LazyRow {
                item {
                    val size = headerList.size
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(numColumns),
                        //Will need to change how height is set up later
                        modifier = Modifier
                            .width((numColumns * 128).dp)
                            .height(((size) * 30).dp),
                    ) {
                        items(headerList) {
                            Text(it, columnHeaderModifier, fontSize = 10.sp)
                        }
                        items(dataList) { Text(it, dataModifier, fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}

fun findMostNames(
    busStopIdList: MutableList<String>,
    stopNamesPerTrip: Map<String, MutableList<String>>
): MutableList<String> {
    var currentLongestList = mutableListOf<String>()
    busStopIdList.forEach { id ->
        val newList = stopNamesPerTrip[id]!!
        if (newList.size > currentLongestList.size) {
            currentLongestList = newList
        }
    }
    return currentLongestList
}
