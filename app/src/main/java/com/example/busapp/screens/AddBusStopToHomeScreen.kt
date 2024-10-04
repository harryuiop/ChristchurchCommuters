package com.example.busapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Search
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.busapp.models.BusStop
import com.example.busapp.viewmodels.AddBusStopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBusStop(
    addBusStopViewModel: AddBusStopViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Add Bus Stop",
                style = TextStyle(fontSize = 30.sp),
                modifier = Modifier.padding(8.dp)
            )
        }

        OutlinedTextField(
            value = addBusStopViewModel.userQuery,
            onValueChange = {newQuery -> addBusStopViewModel.updateQuery(newQuery)},
            label = { Text("Search") }
        )

        LazyColumn {
            itemsIndexed(BusStop.getBusStops()) { index, busStop ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = busStop.stopName)
                }
            }
        }
    }
}