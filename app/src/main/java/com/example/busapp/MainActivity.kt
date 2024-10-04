package com.example.busapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.busapp.models.FileData
import com.example.busapp.screens.ViewTimetables
import com.example.busapp.ui.theme.BusAppTheme
import com.example.busapp.viewmodels.TimetableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val timetableViewModel: TimetableViewModel = viewModel()
            CoroutineScope(Dispatchers.IO).launch {
                val fileData = readFiles(this@MainActivity)
                timetableViewModel.setData(fileData)
            }
            BusAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Bus App") },
                            navigationIcon = {
                                IconButton(onClick = {navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController)
                            }
                            composable("Timetables") {
                                ViewTimetables(navController = navController, timetableViewModel = timetableViewModel)
                            }
                            composable("RouteFinder") {
                                //FindRoute(navController = navController)
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
@Composable
fun Home(navController: NavController, /*busStopViewModel: BusStopViewModel*/) {
    Column (
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Christchurch Commuters", fontSize = 24.sp)

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = "Upcoming", fontSize = 12.sp)

        //busStopViewModel.getUserBusStops()
        //val savedBusStops: List<BusStop> by busStopViewModel.busStops.collectAsState(emptyList())
        LazyColumn {
//            items(busStops) { busStop ->
//                BusStopItem(
//                    busStop = busStop
//                )
//            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Button(
            onClick = { navController.navigate("AddStop") },
            modifier = Modifier.fillMaxWidth().border(8.dp, Color.White, shape = RectangleShape),
            colors = ButtonColors(Color.White, Color.Black, Color.White, Color.Black)
        ) {
            Text("Add Bus Stop")
        }

        Spacer(modifier = Modifier.size(24.dp))

        Row {
            Button(onClick = { navController.navigate("Timetables")}, modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(text = "Timetables")
            }
            Button(onClick = { navController.navigate("RouteFinder") }) {
                Text(text = "Route Finder")
            }
        }
    }
}

suspend fun readFiles(context: Context) = coroutineScope {
    val routes = mutableListOf<List<String?>>()
    val stopTimesPerTrip = HashMap<String?, MutableList<Pair<String?, String?>>>()
    val stopsHashMap = HashMap<String?, String?>()

    val tripsPerRoute = HashMap<String?, MutableList<String?>>()
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.routes)))
        var line: String?
        var counter = 0
        while (reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line?.split(",")
                //Adding route_id, route_short_name, route_long_name
                routes.add(listOf(listLine?.get(0), listLine?.get(2), listLine?.get(3)))
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.trips)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line?.split(",")
                //Adding route_id, trip_id, direction_id
                val tripId = listLine?.get(0)
                if (!tripsPerRoute.containsKey(tripId)) {
                    tripsPerRoute[tripId] = mutableListOf(listLine?.get(2))
                } else {
                    val currentList = tripsPerRoute[tripId]
                    currentList?.add(listLine?.get(2))
                    if (currentList != null) {
                        tripsPerRoute[tripId] = currentList
                    }
                }
            }
            counter++
        }
        reader = BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.stop_times)))
        counter = 0
        while(reader.readLine().also { line = it } != null) {
            if (counter > 0) {
                val listLine = line?.split(",")
                //Adding trip_id, arrival_time, stop_id
                //Only stop times with timepoint = 1 are displayed on metro website
                if (listLine?.get(9) == "1") {
                    val tripId = listLine?.get(0)
                    if (!stopTimesPerTrip.containsKey(tripId)) {
                        stopTimesPerTrip[tripId] = mutableListOf(Pair(listLine?.get(1), listLine?.get(3)))
                    } else {
                        val currentList = stopTimesPerTrip[tripId]
                        currentList?.add(Pair(listLine?.get(1), listLine?.get(3)))
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
                val listLine = line?.split(",")
                //Adding stop_id, stop_name
                stopsHashMap[listLine?.get(0)] = listLine?.get(2)
            }
            counter++
        }
    } catch (e: Exception) {
        Log.e("Reading Metro Files","Error: ${e.message}")
    } finally {
        try {
            reader?.close()
        } catch (e: Exception) {
            Log.e("Reading Metro Files","Error: ${e.message}")
        }
    }
    val stopNamesPerTrip = HashMap<String?, MutableList<String?>>()
    stopTimesPerTrip.forEach { (key, value) ->
        val stopNamesList = mutableListOf<String?>()
        value.forEach { stopNamePair ->
            stopNamesList.add(stopsHashMap[stopNamePair.second])
        }
        stopNamesPerTrip[key] = stopNamesList
    }

    println("coroutine")
    println(tripsPerRoute)

    return@coroutineScope FileData(routes, tripsPerRoute, stopTimesPerTrip, stopNamesPerTrip)
}
