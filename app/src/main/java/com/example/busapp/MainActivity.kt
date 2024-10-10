package com.example.busapp

import RouteFinder
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.busapp.models.BusStop
import com.example.busapp.models.GtfsRealtimeFeed
import com.example.busapp.models.LiveBusViaStop
import com.example.busapp.models.UserData
import com.example.busapp.screens.AddBusStop
import com.example.busapp.screens.ViewTimetables
import com.example.busapp.services.MetroApiService
import com.example.busapp.services.readMetroFiles
import com.example.busapp.ui.theme.BusAppTheme
import com.example.busapp.viewmodels.AddBusStopViewModel
import com.example.busapp.viewmodels.GtfsRealTimeViewModel
import com.example.busapp.viewmodels.RouteFinderViewModel
import com.example.busapp.viewmodels.TimetableViewModel
import com.example.busapp.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel


class MainActivity : ComponentActivity() {


    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val routeFinderViewModel: RouteFinderViewModel by koinViewModel()
            var isDarkTheme by remember { mutableStateOf(true) }
            val timetableViewModel: TimetableViewModel = viewModel()
            val gftsRealTimeViewModel: GtfsRealTimeViewModel = viewModel()
            val addBusStopViewModel: AddBusStopViewModel by koinViewModel()
            val userViewModel: UserViewModel by koinViewModel()
            val metroApiService = MetroApiService()

            userViewModel.addUser(UserData(0, BusStop(-1, "")))

            CoroutineScope(Dispatchers.IO).launch {
                val lifeData: GtfsRealtimeFeed = metroApiService.getRealTimeData()
                gftsRealTimeViewModel.setData(lifeData)
            }

            CoroutineScope(Dispatchers.IO).launch {
                val fileData = readMetroFiles(this@MainActivity)
                timetableViewModel.setData(fileData)

                withContext(Dispatchers.Main) {
                    addBusStopViewModel.addBusStops(fileData.stopsHashMap)
                }
            }


            BusAppTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(id = R.string.app_title)) },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(id = R.string.icon_content_desc_back)
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController, gftsRealTimeViewModel= gftsRealTimeViewModel, timetableViewModel = timetableViewModel, metroApiService = metroApiService, lifecycleScope = lifecycleScope, addBusStopViewModel = addBusStopViewModel, userViewModel = userViewModel)
                            }
                            composable("Timetables") {
                                ViewTimetables(navController = navController, timetableViewModel = timetableViewModel)
                            }
                            composable("RouteFinder") {
                                RouteFinder(navController = navController, routeFinderViewModel)
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
    addBusStopViewModel: AddBusStopViewModel,
    userViewModel: UserViewModel
) {
    val feed by gftsRealTimeViewModel.feed.collectAsState()

    var isLoading by remember { mutableStateOf(true) }

    userViewModel.getAllUsers()
    val users by userViewModel.users.collectAsState(emptyList())
    val user by userViewModel.user.collectAsState()
    var tripUpdatesContainingStopId by remember { mutableStateOf<List<LiveBusViaStop>>(emptyList()) }
    var refreshedData by remember { mutableStateOf(GtfsRealtimeFeed(
        lastUpdated = Date(0),
        tripUpdates = emptyList())) }


    LaunchedEffect(users,tripUpdatesContainingStopId) {
        if (users.isNotEmpty()) {
            isLoading = false
            userViewModel.getUserById(0)
        } else {
            userViewModel.addUser(UserData(0, BusStop(-1, "")))
        }
    }

    if(isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (user!!.selectedStop.id != -1){
            tripUpdatesContainingStopId = getRelevantTripUpdates(gftsRealTimeViewModel.feed.value, user!!.selectedStop.id.toString()).sortedBy { it.arrivalTime }

        } else {
            userViewModel.getUserById(0)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.home_title), fontSize = 24.sp)

            Spacer(modifier = Modifier.size(12.dp))

            if (user!!.selectedStop.id != -1) {
                Text(
                    text = "${R.string.your_stop}\n#${user?.selectedStop?.id} ${user?.selectedStop?.stopName}",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Text(text = "${R.string.upcoming} ${convertDateToTime(refreshedData.lastUpdated)}", fontSize = 12.sp)
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            val liveData: GtfsRealtimeFeed = metroApiService.getRealTimeData()
                            Log.d("Home", "Fetched new data: ${liveData.tripUpdates.size} trip updates")
                            gftsRealTimeViewModel.setData(liveData)
                            refreshedData = liveData
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh, 
                        contentDescription = stringResource(id = R.string.icon_content_desc_refresh)
                    )
                    Text(stringResource(id = R.string.refresh))
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    items(tripUpdatesContainingStopId) { tripUpdate ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.tripId]]?.second} " +
                                        "${timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.tripId]]?.first}",
                                    fontWeight = FontWeight.Bold, fontSize = 20.sp )
                                Text("${stringResource(id = R.string.due)} ${arrivalIn(tripUpdate.arrivalTime)}")
                                Text("${stringResource(id = R.string.direction)} ${timetableViewModel.tripIdToHeadboard.value[tripUpdate.tripId]}")
                                Text("${stringResource(id = R.string.scheduled_arrival)} ${convertDateToTime(tripUpdate.arrivalTime)}")
                                when (tripUpdate.scheduleRelationship) {
                                    "SCHEDULED" -> Text(stringResource(id = R.string.trip_scheduled))
                                    "ADDED" -> Text(stringResource(id = R.string.trip_added))
                                    "CANCELED" -> Text(stringResource(id = R.string.trip_cancelled))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))

            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {}
            }


            Row {
                Button(
                    onClick = { navController.navigate("AddStop") },
                    modifier = Modifier
                ) {
                    Text(
                        if (user!!.selectedStop.id == -1)
                            stringResource(id = R.string.add_bus_stop)
                        else
                            stringResource(id = R.string.change_bus_stop)
                    )
                }
            }


            Spacer(modifier = Modifier.size(24.dp))


                Row {
                    Column {
                        Button(
                            onClick = { navController.navigate("Timetables") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = stringResource(id = R.string.icon_content_desc_timetables),
                                modifier = Modifier.size(30.dp)
                            )
                            Text(stringResource(id = R.string.button_text_timetables))
                        }
                    }

                    Column {
                        Button(
                            onClick = { navController.navigate("RouteFinder") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Map, 
                                contentDescription = stringResource(id = R.string.icon_content_desc_routes),
                                modifier = Modifier.size(30.dp)
                            )
                            Text(stringResource(id = R.string.button_text_routes))
                        }
                    }
                }

            Spacer(modifier = Modifier.size(12.dp))
        }
    }
}

fun getRelevantTripUpdates(feed: GtfsRealtimeFeed, stopId: String): List<LiveBusViaStop> {
    var relevantTrips: List<LiveBusViaStop> = emptyList()
    feed.tripUpdates.forEach { trip ->
        trip.stopTimeUpdates.forEach { stop ->
            if (stop.stopId == stopId) {
                relevantTrips = relevantTrips + LiveBusViaStop(trip.tripId, stop.stopId, trip.scheduleRelationship, stop.arrival!!.delay, stop.departure!!.delay, stop.arrival.time, stop.departure.time)
            }
        }
    }
    return relevantTrips
}


@Composable
@SuppressLint("DefaultLocale")
fun arrivalIn(date: Date?): String {
    if (date == null) return stringResource(id = R.string.not_available)

    val now = Date()
    val differenceInMillis = date.time - now.time
    val differenceInSeconds = differenceInMillis / 1000

    val minutes = ((differenceInSeconds % 3600) / 60)

    return if (minutes > 0) {
        String.format(" in %2d minutes", minutes)
    } else if (minutes.toInt() == 1) {
        String.format(" in %2d minute", minutes)
    } else {
        " ${stringResource(id = R.string.now)}"
    }
}

@Composable
fun convertDateToTime(date: Date?): String {
    if (date == null) return stringResource(id = R.string.not_available)
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
}