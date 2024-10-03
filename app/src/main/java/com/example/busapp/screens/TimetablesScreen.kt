package com.example.busapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color

@Composable
fun ViewTimetables(
    navController: NavController
) {
    val context = LocalContext.current
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

                Button(
                    onClick = {}
                ) {
                    Text("Search Bus", fontStyle = FontStyle.Italic)
                }

                Spacer(modifier = Modifier.size(20.dp))

                Text(text = "No Bus Selected", fontSize = 20.sp)

                Button(
                    onClick = {}
                ) {
                    Text("View Other Direction", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {}
                    ) {
                        Text("Weekday")
                    }
                    Button(
                        onClick = {}
                    ) {
                        Text("Saturday")
                    }
                    Button(
                        onClick = {}
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
        val headerList = listOf("Eastgate Mall (Buckleys Rd)", "St Martins Shops", "Princess Margaret Hospital", "Barrington Mall (Barrington St)", "Westfield Riccarton", "Burnside High School", "Northlands Platform B", "The Palms(North Parade)", "Eastgate Mall (Buckleys Road)")
        val dataList = listOf("12:30pm","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a")

        val columnHeaderModifier = Modifier.border(1.dp, Color.Black).wrapContentSize().padding(4.dp)
        val dataModifier = Modifier.border(1.dp, Color.Black).wrapContentSize().padding(4.dp)

        //TODO: Change value in here to num stops in route
        val numColumns = 9
        item {
            LazyRow {
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(numColumns),
                        //Will need to change how height is set up later
                        modifier = Modifier.width((numColumns*128).dp).height(((headerList.size)*30).dp),
                    ) {
                        items(headerList) { Text("$it", columnHeaderModifier, fontSize = 10.sp) }
                        items(dataList) { Text("$it", dataModifier, fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}