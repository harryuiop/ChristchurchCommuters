package com.example.busapp

import RouteFinder
import android.os.Bundle
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
import com.example.busapp.ui.theme.BusAppTheme
import com.example.busapp.viewmodels.RouteFinderViewmodel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
                        val routeFinderViewmodel: RouteFinderViewmodel = viewModel()

                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController)
                            }
                            composable("Timetables") {
                                //ViewTimetables(navController = navController)
                            }
                            composable("RouteFinder") {
                                RouteFinder(navController = navController, routeFinderViewmodel)
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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
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
            modifier = Modifier
                .fillMaxWidth()
                .border(8.dp, Color.White, shape = RectangleShape),
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