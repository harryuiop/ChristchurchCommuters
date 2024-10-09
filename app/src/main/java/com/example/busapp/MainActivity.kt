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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
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
import com.example.busapp.models.BusStop
import com.example.busapp.models.FileData
import com.example.busapp.models.GtfsRealtimeFeed
import com.example.busapp.models.StopTimeUpdate
import com.example.busapp.models.TripUpdate
import com.example.busapp.models.UserData
import com.example.busapp.screens.AddBusStop
import com.example.busapp.screens.ViewTimetables
import com.example.busapp.services.readMetroFiles
import com.example.busapp.services.MetroApiService
import com.example.busapp.ui.theme.BusAppTheme
import com.example.busapp.viewmodels.AddBusStopViewModel
import com.example.busapp.viewmodels.GtfsRealTimeViewModel
import com.example.busapp.viewmodels.TimetableViewModel
import com.example.busapp.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            val userViewModel: UserViewModel by koinViewModel()

            val gftsRealTimeViewModel: GtfsRealTimeViewModel = viewModel()

            LaunchedEffect(Unit) {
                userViewModel.getAllUsers()
                if (userViewModel.user.value.isEmpty()) {
                    userViewModel.addUser(UserData(0, BusStop(-1, "")))
                }
                userViewModel.getAllUsers()
            }


            CoroutineScope(Dispatchers.IO).launch {
                val fileData = readMetroFiles(this@MainActivity)
                timetableViewModel.setData(fileData)

                withContext(Dispatchers.Main) {

                    addBusStopViewModel.addBusStops(fileData.stopsHashMap)

                }
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
                                Home(navController = navController, gftsRealTimeViewModel= gftsRealTimeViewModel, timetableViewModel = timetableViewModel, metroApiService = metroApiService, lifecycleScope = lifecycleScope, stopId = "23940", addBusStopViewModel = addBusStopViewModel, userViewModel = userViewModel)
                            }
                            composable("Timetables") {
                                ViewTimetables(navController = navController, timetableViewModel = timetableViewModel)
                            }
                            composable("RouteFinder") {
                                RouteFinder(navController = navController)
                            }
                            composable("AddStop") {
                                AddBusStop(navController = navController, userViewModel = userViewModel,
                                    addBusStopViewModel = addBusStopViewModel)
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
fun Home(
    navController: NavController,
    gftsRealTimeViewModel: GtfsRealTimeViewModel,
    timetableViewModel: TimetableViewModel,
    metroApiService: MetroApiService,
    lifecycleScope: CoroutineScope,
    stopId: String,
    addBusStopViewModel: AddBusStopViewModel,
    userViewModel: UserViewModel
         ) {
    val feed by gftsRealTimeViewModel.feed.collectAsState()
    val user by userViewModel.user.collectAsState()
    userViewModel.getAllUsers()

    val context = LocalContext.current

    var refreshedData by remember { mutableStateOf(GtfsRealtimeFeed(
        lastUpdated = Date(0),
        tripUpdates = emptyList())) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Christchurch Commuters", fontSize = 24.sp)

        Spacer(modifier = Modifier.size(12.dp))

//        Log.i("USERTEST", "Current user: ${userViewModel.user.value.first()}")

        if (userViewModel.user.value.isNotEmpty() ) {
            Text(text = "Bus Stop #${userViewModel.user.value.first().selectedStop.id}\n${userViewModel.user.value.first().selectedStop.stopName}", fontSize = 16.sp)
            val tripUpdatesContainingStopId: List<Pair<TripUpdate, StopTimeUpdate>> =
                getRelevantTripUpdates(
                    gftsRealTimeViewModel.feed.value,
                    userViewModel.user.value.first().selectedStop.id.toString()
                )
            Text(
                text = "Upcoming - Last updated ${formatTime(refreshedData.lastUpdated)}",
                fontSize = 12.sp
            )
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
                            Text(
                                "${timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.first.tripId]]?.second} " +
                                        "${timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.first.tripId]]?.first}",
                                fontWeight = FontWeight.Bold, fontSize = 20.sp
                            )
                            Text("")
                            Text("Expected Arrival: ${formatTime(tripUpdate.second.arrival?.time)}")
                            Text("Expected Departure: ${formatTime(tripUpdate.second.departure?.time)}")
                            Text("Schedule: ${tripUpdate.first.scheduleRelationship}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

        }

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