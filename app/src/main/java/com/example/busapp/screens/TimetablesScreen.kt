package com.example.busapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
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

        val itemsList = (0..5).toList()
        val itemsIndexedList = listOf("A", "B", "C")

        val itemModifier = Modifier.border(1.dp, Color.Black).width(80.dp)

        LazyHorizontalGrid(
            //TODO: Change value in here to num stops in route
            rows = GridCells.Fixed(3),
        ) {
            items(itemsList) { Text("Item is $it", itemModifier) }

            item { Text("Single item", itemModifier) }

            itemsIndexed(itemsIndexedList) { index, item ->
                Text("Item at index $index is $item", itemModifier)
            }
        }
    }
}