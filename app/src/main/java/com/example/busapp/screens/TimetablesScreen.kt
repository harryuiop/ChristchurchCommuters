package com.example.busapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.busapp.R
import com.example.busapp.viewmodels.TimetableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
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

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedRouteName by rememberSaveable { mutableStateOf("")}
    var selectedRouteId by rememberSaveable { mutableStateOf("") }
    var selectedDay by rememberSaveable { mutableStateOf("") }
    var zeroDirection by rememberSaveable { mutableStateOf(true) }
    var headerList by rememberSaveable { mutableStateOf(mutableListOf<String>()) }
    var dataList by rememberSaveable { mutableStateOf(mutableListOf<String>()) }
    var numColumns by rememberSaveable { mutableIntStateOf(1) }
    var numRows by rememberSaveable { mutableDoubleStateOf(1.0) }

    val weekdayVisible by rememberSaveable { mutableStateOf(true) }
    var saturdayVisible by rememberSaveable { mutableStateOf(true) }
    var sundayVisible by rememberSaveable { mutableStateOf(true) }
    var weekdayClicked by rememberSaveable { mutableStateOf(false) }
    var saturdayClicked by rememberSaveable { mutableStateOf(false) }
    var sundayClicked by rememberSaveable { mutableStateOf(false) }
    var progressVisible by rememberSaveable { mutableStateOf(false) }
    var tableVisible by rememberSaveable { mutableStateOf(false) }
    val tableBackgroundColour = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
    val selectedButtonColour = if (isSystemInDarkTheme()) Color.hsl(223F, 0.74F, 0.35F) else Color.Black
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.timetables_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(20.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        value = selectedRouteName,
                        placeholder = { Text(stringResource(id = R.string.text_placeholder_select_bus)) },
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

                                    expanded = false
                                    weekdayClicked = true
                                    saturdayClicked = false
                                    sundayClicked = false
                                    progressVisible = true
                                    tableVisible = false
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (route[0] == "Oc_36_3") {
                                            zeroDirection = false
                                            headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection1[route[0]]!!, stopNamesPerTrip)
                                            dataList = getDataList(mondayToFridayTripsPerRouteDirection1[route[0]]!!, stopTimesPerTrip)
                                        } else {
                                            zeroDirection = true
                                            headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection0[route[0]]!!, stopNamesPerTrip)
                                            dataList = getDataList(mondayToFridayTripsPerRouteDirection0[route[0]]!!, stopTimesPerTrip)
                                            if (!sundayTripsPerRouteDirection0.containsKey(route[0])) {
                                                sundayVisible = false
                                            } else {
                                                sundayVisible = true
                                            }
                                            if (!saturdayTripsPerRouteDirection0.containsKey(route[0])) {
                                                saturdayVisible = false
                                            } else {
                                                saturdayVisible = true
                                            }
                                        }
                                        numColumns = headerList.size
                                        numRows = ceil((((headerList.size + dataList.size) / headerList.size).toDouble()))
                                    }
                                })
                        }
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Button(
                    onClick = {
                        progressVisible = true
                        tableVisible = false
                        CoroutineScope(Dispatchers.IO).launch {
                            if (selectedRouteId == "Oc_36_3") {
                                selectedRouteId = "Oa_36_3"
                                selectedRouteName = "Oa - Orbiter"
                                when (selectedDay) {
                                    "1" -> {
                                        headerList = findHeaderNames(sundayTripsPerRouteDirection0["Oa_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(sundayTripsPerRouteDirection0["Oa_36_3"]!!, stopTimesPerTrip)
                                    }
                                    "2" -> {
                                        headerList = findHeaderNames(fridayTripsPerRouteDirection0["Oa_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(fridayTripsPerRouteDirection0["Oa_36_3"]!!, stopTimesPerTrip)
                                    }
                                    "3" -> {
                                        headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection0["Oa_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(mondayToFridayTripsPerRouteDirection0["Oa_36_3"]!!, stopTimesPerTrip)
                                    }
                                    "4" -> {
                                        headerList = findHeaderNames(saturdayTripsPerRouteDirection0["Oa_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(saturdayTripsPerRouteDirection0["Oa_36_3"]!!, stopTimesPerTrip)
                                    }
                                }
                            } else if (selectedRouteId == "Oa_36_3") {
                                selectedRouteId = "Oc_36_3"
                                selectedRouteName = "Oc - Orbiter"
                                when (selectedDay) {
                                    "1" -> {
                                        headerList = findHeaderNames(sundayTripsPerRouteDirection1["Oc_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(sundayTripsPerRouteDirection1["Oc_36_3"]!!, stopTimesPerTrip)
                                    }
                                    "2" -> {
                                        headerList = findHeaderNames(fridayTripsPerRouteDirection1["Oc_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(fridayTripsPerRouteDirection1["Oc_36_3"]!!, stopTimesPerTrip)
                                    }
                                    "3" -> {
                                        headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection1["Oc_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(mondayToFridayTripsPerRouteDirection1["Oc_36_3"]!!, stopTimesPerTrip)
                                    }
                                    "4" -> {
                                        headerList = findHeaderNames(saturdayTripsPerRouteDirection1["Oc_36_3"]!!, stopNamesPerTrip)
                                        dataList = getDataList(saturdayTripsPerRouteDirection1["Oc_36_3"]!!, stopTimesPerTrip)
                                    }
                                }
                            } else if (selectedRouteId != "") {
                                zeroDirection = !zeroDirection
                                if (zeroDirection) {
                                    when (selectedDay) {
                                        "1" -> {
                                            headerList = findHeaderNames(sundayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(sundayTripsPerRouteDirection0[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                        "2" -> {
                                            headerList = findHeaderNames(fridayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(fridayTripsPerRouteDirection0[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                        "3" -> {
                                            headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(mondayToFridayTripsPerRouteDirection0[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                        "4" -> {
                                            headerList = findHeaderNames(saturdayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(saturdayTripsPerRouteDirection0[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                    }
                                } else {
                                    when (selectedDay) {
                                        "1" -> {
                                            headerList = findHeaderNames(sundayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(sundayTripsPerRouteDirection1[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                        "2" -> {
                                            headerList = findHeaderNames(fridayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(fridayTripsPerRouteDirection1[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                        "3" -> {
                                            headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(mondayToFridayTripsPerRouteDirection1[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                        "4" -> {
                                            headerList = findHeaderNames(saturdayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(saturdayTripsPerRouteDirection1[selectedRouteId]!!, stopTimesPerTrip)
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.view_other_direction), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (weekdayVisible) {
                        Button(
                            onClick = {
                                if (selectedRouteId != "") {
                                    progressVisible = true
                                    tableVisible = false
                                    selectedDay = "3"
                                    saturdayClicked = false
                                    sundayClicked = false
                                    weekdayClicked = true
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (zeroDirection) {
                                            headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(mondayToFridayTripsPerRouteDirection0[selectedRouteId]!!, stopTimesPerTrip)
                                            numColumns = headerList.size
                                            numRows = ceil((((headerList.size + dataList.size) / headerList.size).toDouble()))
                                        } else {
                                            headerList = findHeaderNames(mondayToFridayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(mondayToFridayTripsPerRouteDirection1[selectedRouteId]!!, stopTimesPerTrip)
                                            numColumns = headerList.size
                                            numRows = ceil((((headerList.size + dataList.size) / headerList.size).toDouble()))
                                        }
                                    }
                                }
                            },
                            colors = if (weekdayClicked) {
                                ButtonColors(selectedButtonColour, Color.White, Color.Gray, Color.White)
                            } else {
                                ButtonColors(ButtonDefaults.buttonColors().containerColor, ButtonDefaults.buttonColors().contentColor, Color.Gray, Color.White)
                            }
                        ) {
                            Text(stringResource(id = R.string.weekday))
                        }
                    }
                    if (saturdayVisible) {
                        Button(
                            onClick = {
                                if (selectedRouteId != "") {
                                    progressVisible = true
                                    tableVisible = false
                                    selectedDay = "4"
                                    weekdayClicked = false
                                    sundayClicked = false
                                    saturdayClicked = true
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (zeroDirection) {
                                            headerList = findHeaderNames(saturdayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(saturdayTripsPerRouteDirection0[selectedRouteId]!!, stopTimesPerTrip)
                                            numColumns = headerList.size
                                            numRows = ceil((((headerList.size + dataList.size) / headerList.size).toDouble()))
                                        } else {
                                            headerList = findHeaderNames(saturdayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(saturdayTripsPerRouteDirection1[selectedRouteId]!!, stopTimesPerTrip)
                                            numColumns = headerList.size
                                            numRows = ceil((((headerList.size + dataList.size) / headerList.size).toDouble()))
                                        }
                                    }
                                }
                            },
                            colors = if (saturdayClicked) {
                                ButtonColors(selectedButtonColour, Color.White, Color.Gray, Color.White)
                            } else {
                                ButtonColors(ButtonDefaults.buttonColors().containerColor, ButtonDefaults.buttonColors().contentColor, Color.Gray, Color.White)
                            }
                        ) {
                            Text(stringResource(id = R.string.saturday))
                        }
                    }
                    if (sundayVisible) {
                        Button(
                            onClick = {
                                if (selectedRouteId != "") {
                                    progressVisible = true
                                    tableVisible = false
                                    selectedDay = "1"
                                    weekdayClicked = false
                                    saturdayClicked = false
                                    sundayClicked = true
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (zeroDirection) {
                                            headerList = findHeaderNames(sundayTripsPerRouteDirection0[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(sundayTripsPerRouteDirection0[selectedRouteId]!!, stopTimesPerTrip)
                                            numColumns = headerList.size
                                            numRows = ceil((((headerList.size + dataList.size) / headerList.size).toDouble()))
                                        } else {
                                            headerList = findHeaderNames(sundayTripsPerRouteDirection1[selectedRouteId]!!, stopNamesPerTrip)
                                            dataList = getDataList(sundayTripsPerRouteDirection1[selectedRouteId]!!, stopTimesPerTrip)
                                            numColumns = headerList.size
                                            numRows = ceil((((headerList.size + dataList.size) / headerList.size).toDouble()))
                                        }
                                    }
                                }
                            },
                            colors = if (sundayClicked) {
                                ButtonColors(selectedButtonColour, Color.White, Color.Gray, Color.White)
                            } else {
                                ButtonColors(ButtonDefaults.buttonColors().containerColor, ButtonDefaults.buttonColors().contentColor, Color.Gray, Color.White)
                            }
                        ) {
                            Text(stringResource(id = R.string.sunday))
                        }
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                LaunchedEffect(key1 = progressVisible, key2 = tableVisible) {
                    if (progressVisible) {
                        delay(2000)
                        progressVisible = false
                        tableVisible = true
                    }
                }

                if (progressVisible) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }

            }
        }

        val columnHeaderModifier = Modifier
            .border(1.dp, Color.Black)
            .padding(4.dp)
            .size(50.dp)
        val dataModifier = Modifier
            .border(1.dp, Color.Black)
            .size(35.dp)
            .wrapContentSize()
            .padding(4.dp)

        item {
            LazyRow {
                item {
                    if (tableVisible) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(numColumns),
                            modifier = Modifier
                                .width((numColumns * 128).dp)
                                .height(((numRows - 1) * 35 + 52).dp)
                                .border(2.dp, Color.Black)
                                .background(tableBackgroundColour)
                        ) {
                            items(headerList) { headerItem ->
                                Text(
                                    text = headerItem,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .border(1.dp, Color.Gray)
                                        .background(Color(0xFFD0BCFF))
                                        .padding(4.dp)
                                        .size(50.dp),
                                    fontSize = 10.sp,
                                )
                            }
                            items(dataList) { dataItem ->
                                Text(
                                    text = dataItem,
                                    color = Color(0xFFCCC2DC),
                                    modifier = Modifier
                                        .border(1.dp, Color.Gray)
                                        .background(Color(0xFF625b71))
                                        .size(35.dp)
                                        .wrapContentSize()
                                        .padding(4.dp),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun findHeaderNames(
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

fun findLongestStopSequence(
    tripsPerRoute: MutableList<String>,
    stopTimesPerTrip: Map<String, MutableList<Pair<String, String>>>
): List<String> {
    var currentLongestList = listOf<String>()
    tripsPerRoute.forEach { tripId ->
        val stops = stopTimesPerTrip[tripId]!!
        if (stops.size > currentLongestList.size) {
            currentLongestList = stops.map { it.second }
        }
    }
    return currentLongestList
}

fun getDataList(
    tripsPerRoute: MutableList<String>,
    stopTimesPerTrip: Map<String, MutableList<Pair<String, String>>>
): MutableList<String> {
    val finalList = mutableListOf<String>()
    val stopSequence = findLongestStopSequence(tripsPerRoute, stopTimesPerTrip)
    tripsPerRoute.sortedBy {it.toInt()}.forEach { tripId ->
        println(tripId)
        val stops = stopTimesPerTrip[tripId]!!
        println(stops)
        val currentStops = mutableListOf<String>()
        var curIndex = 0
        stopSequence.forEach { pair ->
            if (curIndex < stops.size && stops[curIndex].second == pair) {
                currentStops.add(stops[curIndex].first)
                curIndex++
            } else {
                currentStops.add("")
            }
        }
        finalList.addAll(currentStops)
    }
    return finalList
}
