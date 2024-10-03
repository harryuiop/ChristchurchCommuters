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
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.GridCells
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

        //TODO: Get the data from the stop_times/trips/stops files
        val itemsList = listOf("Eastgate Mall (Buckleys Rd)", "St Martins Shops", "Princess Margaret Hospital", "Barrington Mall (Barrington St)", "Westfield Riccarton", "Burnside High School", "Northlands Platform B", "The Palms(North Parade)", "Eastgate Mall (Buckleys Road)", "a","a","a","a","a","a","a","a","a","a")

        val itemModifier = Modifier.border(1.dp, Color.Black).wrapContentSize().padding(2.dp)

        //TODO: Change value in here to num stops in route
        val numRows = 19
        item{
            LazyHorizontalGrid(
                rows = GridCells.Fixed(numRows),
                modifier = Modifier.height((numRows*30).dp),
            ) {
                items(itemsList) { Text("$it", itemModifier, fontSize = 10.sp) }
            }
        }
    }
}