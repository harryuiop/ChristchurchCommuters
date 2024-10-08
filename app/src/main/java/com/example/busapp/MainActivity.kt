package com.example.busapp

import RouteFinder
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors

import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.busapp.models.FileData
import com.example.busapp.models.GtfsRealtimeFeed
import com.example.busapp.models.StopTimeUpdate
import com.example.busapp.models.TripUpdate
import com.example.busapp.screens.ViewTimetables
import com.example.busapp.services.readMetroFiles
import com.example.busapp.services.MetroApiService
import com.example.busapp.ui.theme.BusAppTheme
import com.example.busapp.viewmodels.AddBusStopViewModel
import com.example.busapp.viewmodels.GtfsRealTimeViewModel
import com.example.busapp.viewmodels.TimetableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.hours


class MainActivity : ComponentActivity() {

    private val metroApiService = MetroApiService()

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val timetableViewModel: TimetableViewModel = viewModel()
            val addBusStopViewModel: AddBusStopViewModel by koinViewModel()

            val gftsRealTimeViewModel: GtfsRealTimeViewModel = viewModel()

            CoroutineScope(Dispatchers.IO).launch {
                val fileData = readMetroFiles(this@MainActivity)
                timetableViewModel.setData(fileData)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val lifeData: GtfsRealtimeFeed = metroApiService.getRealTimeData()
                gftsRealTimeViewModel.setData(lifeData)
            }

            BusAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Bus App") },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController, gftsRealTimeViewModel= gftsRealTimeViewModel, timetableViewModel = timetableViewModel, metroApiService = metroApiService, lifecycleScope = lifecycleScope, stopId = "23940")
                            }
                            composable("Timetables") {
                                ViewTimetables(navController = navController, timetableViewModel = timetableViewModel)
                            }
                            composable("RouteFinder") {
                                RouteFinder(navController = navController)
                            }
                            composable("AddStop") {
                                //AddBusStop(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Home(navController: NavController, gftsRealTimeViewModel: GtfsRealTimeViewModel,
                                        timetableViewModel: TimetableViewModel,
                                        metroApiService: MetroApiService,
                                        lifecycleScope: kotlinx.coroutines.CoroutineScope,
                                        stopId: String) {
    val feed by gftsRealTimeViewModel.feed.collectAsState()

    var refreshedData by remember { mutableStateOf(GtfsRealtimeFeed(
        lastUpdated = Date(0),
        tripUpdates = emptyList())) }

    val tripUpdatesContainingStopId: List<Pair<TripUpdate, StopTimeUpdate>> = getRelevantTripUpdates(
                                                                gftsRealTimeViewModel.feed.value,
                                                                stopId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Christchurch Commuters", fontSize = 24.sp)

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = "Upcoming - Last updated ${formatTime(refreshedData.lastUpdated)}", fontSize = 12.sp)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(tripUpdatesContainingStopId) { tripUpdate ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.first.tripId]]?.second} " +
                                  "${timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.first.tripId]]?.first}",
                                    fontWeight = FontWeight.Bold, fontSize = 20.sp )
                        Text("")
                        Text("Expected Arrival: ${formatTime(tripUpdate.second.arrival?.time)}")
                        Text("Expected Departure: ${formatTime(tripUpdate.second.departure?.time)}")
                        Text("Schedule: ${tripUpdate.first.scheduleRelationship}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Button(
            onClick = { navController.navigate("AddStop") },
            modifier = Modifier
                .fillMaxWidth()
                .border(8.dp, Color.White, shape = RectangleShape),
            colors = ButtonColors(Color.White, Color.Black, Color.White, Color.Black)
        ) {
            Text("Add Bus Stop")
        }

        Spacer(modifier = Modifier.size(24.dp))

        Row {
            Button(
                onClick = { navController.navigate("Timetables") },
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(text = "Timetables")
            }
            Button(onClick = { navController.navigate("RouteFinder") }) {
                Text(text = "Route Finder")
            }
        }
        Row {
            Button(onClick = {
                lifecycleScope.launch {
                    val liveData: GtfsRealtimeFeed = metroApiService.getRealTimeData()
                    Log.d("Home", "Fetched new data: ${liveData.tripUpdates.size} trip updates")
                    gftsRealTimeViewModel.setData(liveData)
                    refreshedData = liveData
                }
            }) {
                Text(text = "Refresh Data")
            }
        }

        Spacer(modifier = Modifier.size(12.dp))
    }
}

fun getRelevantTripUpdates(feed: GtfsRealtimeFeed, stopId: String): List<Pair<TripUpdate, StopTimeUpdate>> {
    var relevantTrips: List<Pair<TripUpdate, StopTimeUpdate>> = emptyList()
    feed.tripUpdates.forEach { trip ->
        trip.stopTimeUpdates.forEach { stop ->
            if (stop.stopId == stopId) {
                relevantTrips = relevantTrips + Pair(trip, stop)
            }
        }
    }
    return relevantTrips
}

fun formatTime(date: Date?): String {
    if (date == null) return "N/A"
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(date)
}

suspend fun readFiles(context: Context) = coroutineScope {
    val routes = mutableListOf<List<String>>()
    val stopTimesPerTrip = HashMap<String, MutableList<Pair<String, String>>>()
    val tripIdToHeadboard = HashMap<String, String>()
    val tripIdToRouteID = HashMap<String, String>()
    val tripIdToNameNumber =  HashMap<String, Pair<String, String>>()
    val stopsHashMap = HashMap<String, String>()

    val sundayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()
    val fridayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()
    val mondayToFridayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()
    val saturdayTripsPerRouteDirection0 = HashMap<String, MutableList<String>>()

    val sundayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()
    val fridayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()
    val mondayToFridayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()
    val saturdayTripsPerRouteDirection1 = HashMap<String, MutableList<String>>()

    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.routes)))
        var line: String?
        var counter = 0
        while (reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                val tripId = listLine[0]
                val shortName = listLine[3]
                val number = listLine[2]
                //Adding route_id, route_short_name, route_long_name
                routes.add(listOf(listLine[0], listLine[2], listLine[3]))
                tripIdToNameNumber[tripId] = Pair(shortName, number)
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.trips)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                val id = listLine[2]
                val headboard = listLine[3]
                val routeId = listLine[0]
                val direction = listLine[5]
                if (direction == "0") {
                    when (listLine[1]) {
                        "1" -> addToMap(sundayTripsPerRouteDirection0, listLine)
                        "2" -> addToMap(fridayTripsPerRouteDirection0, listLine)
                        "3" -> addToMap(mondayToFridayTripsPerRouteDirection0, listLine)
                        "4" -> addToMap(saturdayTripsPerRouteDirection0, listLine)
                    }
                } else {
                    when (listLine[1]) {
                        "1" -> addToMap(sundayTripsPerRouteDirection1, listLine)
                        "2" -> addToMap(fridayTripsPerRouteDirection1, listLine)
                        "3" -> addToMap(mondayToFridayTripsPerRouteDirection1, listLine)
                        "4" -> addToMap(saturdayTripsPerRouteDirection1, listLine)
                    }
                }
                tripIdToHeadboard[id] = headboard
                tripIdToRouteID[id] = routeId
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.stop_times)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                //Only stop times with timepoint = 1 are displayed on metro website
                if (listLine[9] == "1") {
                    val tripId = listLine[0]
                    if (!stopTimesPerTrip.containsKey(tripId)) {
                        //Pair of arrival_time and stop_id
                        stopTimesPerTrip[tripId] = mutableListOf(Pair(listLine[1], listLine[3]))
                    } else {
                        val currentList = stopTimesPerTrip[tripId]
                        currentList?.add(Pair(listLine[1], listLine[3]))
                        if (currentList != null) {
                            stopTimesPerTrip[tripId] = currentList
                        }
                    }
                }
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.stops)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line!!.split(",")
                //Adding stop_id, stop_name
                stopsHashMap[listLine[0]] = listLine[2]
            }
            counter++
        }
    } catch (e: Exception) {
        Log.e("Reading Metro Files","Error: ${e.message}")
    } finally {
        try {
            println(stopsHashMap)
            reader?.close()
        } catch (e: Exception) {
            Log.e("Reading Metro Files","Error: ${e.message}")
        }
    }
    val stopNamesPerTrip = HashMap<String, MutableList<String>>()
    stopTimesPerTrip.forEach { (key, value) ->
        val stopNamesList = mutableListOf<String>()
        value.forEach { stopNamePair ->
            stopsHashMap[stopNamePair.second]?.let { stopNamesList.add(it) }
        }
        stopNamesPerTrip[key] = stopNamesList
    }

    return@coroutineScope FileData(
                                routes,
                                sundayTripsPerRouteDirection0,
                                fridayTripsPerRouteDirection0,
                                mondayToFridayTripsPerRouteDirection0,
                                saturdayTripsPerRouteDirection0,
                                sundayTripsPerRouteDirection1,
                                fridayTripsPerRouteDirection1,
                                mondayToFridayTripsPerRouteDirection1,
                                saturdayTripsPerRouteDirection1,
                                tripIdToHeadboard,
                                tripIdToRouteID,
                                tripIdToNameNumber,
                                stopTimesPerTrip,
                                stopNamesPerTrip
                                )
}

fun addToMap(
    toAdd: HashMap<String, MutableList<String>>,
    listLine: List<String>,
) {
    val routeId = listLine[0]
    if (!toAdd.containsKey(routeId)) {
        //Adding trip_id
        toAdd[routeId] = mutableListOf(listLine[2])
    } else {
        val currentList = toAdd[routeId]
        currentList?.add(listLine[2])
        if (currentList != null) {
            toAdd[routeId] = currentList
        }
    }
}