package com.example.busapp

import RouteFinder
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.busapp.models.BusNotificationState
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
import kotlinx.coroutines.delay
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

@Composable
fun BusUpdateNotificationButton(
    context: Context,
    title: String,
    tripUpdate: LiveBusViaStop,
    notificationState: BusNotificationState,
    id: Int
) {
    val currentIcon = if (notificationState.isNotificationEnabled) {
        Icons.Default.NotificationsActive
    } else {
        Icons.Default.NotificationsNone
    }

    // Check if bus has left
    LaunchedEffect(tripUpdate) {
        if (!tripUpdate.arrivalTime.after(Date())) {
            notificationState.isNotificationEnabled = false
            notificationState.hasFiveMinNotified = false
            notificationState.hasOneMinNotified = false
        }
    }

    // Check for notifications
    LaunchedEffect(notificationState.isNotificationEnabled) {
        while (notificationState.isNotificationEnabled) {
            val now = Date()
            val differenceInMillis = tripUpdate.arrivalTime.time - now.time
            val differenceInSeconds = differenceInMillis / 1000
            val minutes = (differenceInSeconds / 60).toInt()

            // Notify at 5 minutes and 1 minute
            if (minutes == 5 && !notificationState.hasFiveMinNotified) {
                showNotification(
                    context,
                    title,
                    context.getString(R.string.notification_due_minutes, minutes),
                    id
                )
                notificationState.hasFiveMinNotified = true
            }

            if (minutes == 1 && !notificationState.hasOneMinNotified) {
                showNotification(
                    context,
                    title,
                    context.getString(R.string.notification_due_minute, minutes),
                    id
                )
                notificationState.hasOneMinNotified = true
            }

            // Check every 30 seconds
            delay(30000)
        }
    }

    // Button to toggle notifications
    IconButton(
        onClick = {
            notificationState.isNotificationEnabled = !notificationState.isNotificationEnabled // Toggle the state
        }
    ) {
        Icon(
            imageVector = currentIcon,
            contentDescription =
            if (notificationState.isNotificationEnabled)
                stringResource(id = R.string.icon_content_desc_noti_on)
            else
                stringResource(id = R.string.icon_content_desc_noti_off)
        )
    }
}

private fun showNotification(
    context: Context,
    title: String,
    content: String,
    id: Int = 0
) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notificationBuilder =
        NotificationCompat.Builder(context, "bus_delays_channel")
            .setSmallIcon(R.drawable.bus)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)

    notificationManager.notify(id, notificationBuilder.build())
}

@Composable
fun NotificationPermRequest() {
    var hasNotificationPermission by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasNotificationPermission = it }
    )

    if (!hasNotificationPermission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LaunchedEffect(Unit) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
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
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    val busNotificationStates = rememberSaveable { mutableMapOf<String, BusNotificationState>() }

    userViewModel.getAllUsers()
    val users by userViewModel.users.collectAsState(emptyList())
    val user by userViewModel.user.collectAsState()
    var tripUpdatesContainingStopId by remember { mutableStateOf<List<LiveBusViaStop>>(emptyList()) }
    var refreshedData by remember { mutableStateOf(GtfsRealtimeFeed(
        lastUpdated = Date(0),
        tripUpdates = emptyList())) }
    refreshedData = feed

    LaunchedEffect(users) {
        if (users.isNotEmpty()) {
            isLoading = false
            userViewModel.getUserById(0)
        } else {
            userViewModel.addUser(UserData(0, BusStop(-1, "")))
        }
    }

    NotificationPermRequest()

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

            Spacer(modifier = Modifier.size(12.dp))

            if (user!!.selectedStop.id != -1) {
                Text(
                    text = "${user?.selectedStop?.stopName} - ${user?.selectedStop?.id} ",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    context.getString(R.string.updated_last, convertDateToTime(refreshedData.lastUpdated))
                )

                Spacer(modifier = Modifier.size(12.dp))


                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    items(tripUpdatesContainingStopId) { tripUpdate ->
                        val tripId = tripUpdate.tripId
                        val notificationState = busNotificationStates.getOrPut(tripId) { BusNotificationState() }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val first = timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.tripId]]?.first
                                    val second = timetableViewModel.tripIdToNameNumber.value[timetableViewModel.tripIdToRouteId.value[tripUpdate.tripId]]?.second

                                    Text(
                                        if (first != null)
                                            "$second $first"
                                        else
                                            stringResource(id = R.string.loading),
                                        fontWeight = FontWeight.Bold, fontSize = 20.sp
                                    )

                                    BusUpdateNotificationButton(
                                        context = context,
                                        title = "$second $first",
                                        tripUpdate = tripUpdate,
                                        notificationState = notificationState,
                                        id = tripId.toInt()
                                    )
                                }

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

            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {}
            }

            Spacer(modifier = Modifier.size(12.dp))

            Row {
                Button(
                    onClick = { navController.navigate("AddStop") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    modifier = Modifier
                ) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Timetables",
                        modifier = Modifier
                            .size(30.dp))
                    Text(
                        if (user!!.selectedStop.id == -1)
                            stringResource(id = R.string.add_stop)
                        else
                            stringResource(id = R.string.change_stop)
                    )
                }

                Button(
                    onClick = {
                        lifecycleScope.launch {
                            val liveData: GtfsRealtimeFeed = metroApiService.getRealTimeData()
                            Log.d("Home", "Fetched new data: ${liveData.tripUpdates.size} trip updates")
                            gftsRealTimeViewModel.setData(liveData)
                            refreshedData = liveData
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh, 
                        contentDescription = stringResource(id = R.string.icon_content_desc_routes)
                    )
                    Text(stringResource(id = R.string.refresh))
                }
            }

            Spacer(modifier = Modifier.size(12.dp))


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
        String.format(stringResource(id = R.string.in_minutes), minutes)
    } else if (minutes.toInt() == 1) {
        String.format(stringResource(id = R.string.in_minute), minutes)
    } else {
        stringResource(id = R.string.now)
    }
}

@Composable
fun convertDateToTime(date: Date?): String {
    if (date == null) return stringResource(id = R.string.not_available)
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
}