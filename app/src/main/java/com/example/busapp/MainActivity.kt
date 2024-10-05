package com.example.busapp

import RouteFinder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.busapp.services.MetroApiService
import com.example.busapp.ui.theme.BusAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val metroApiService = MetroApiService()

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
                                Home(navController = navController, metroApiService = metroApiService, lifecycleScope = lifecycleScope)
                            }
                            composable("Timetables") {
                                //ViewTimetables(navController = navController)
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

@Composable
fun Home(navController: NavController, metroApiService: MetroApiService, lifecycleScope: kotlinx.coroutines.CoroutineScope) {
    var apiResult by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Christchurch Commuters", fontSize = 24.sp)

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = "Upcoming", fontSize = 12.sp)

        LazyColumn {
            // Your existing LazyColumn content
        }

        Spacer(modifier = Modifier.size(12.dp))

        Button(
            onClick = { navController.navigate("AddStop") },
            modifier = Modifier
                .fillMaxWidth()
                .border(8.dp, Color.White, shape = RectangleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
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
                    metroApiService.getRealTimeData()
                }
            }) {
                Text(text = "Refresh Data")
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = apiResult)
    }
}