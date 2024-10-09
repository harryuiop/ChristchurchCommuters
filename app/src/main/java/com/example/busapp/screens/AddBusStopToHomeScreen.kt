package com.example.busapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Search
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.busapp.models.BusStop
import com.example.busapp.viewmodels.AddBusStopViewModel
import com.example.busapp.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBusStop(
    addBusStopViewModel: AddBusStopViewModel,
    userViewModel: UserViewModel,
    navController: NavController
) {

    val busStops: Map<String, String> by addBusStopViewModel.busStops.collectAsState()

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
            onValueChange = { newQuery -> addBusStopViewModel.updateQuery(newQuery) },
            label = { Text("Search") },
            placeholder = { Text("Type Stop # or Location") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .padding(bottom = 16.dp)
        ) {
            itemsIndexed(busStops.entries.toList()) { index, (key, value) ->
                if (value.contains(addBusStopViewModel.userQuery, ignoreCase = true) || key.contains(addBusStopViewModel.userQuery, ignoreCase = true)) {
                    OutlinedCard(
                        onClick = {
                            Log.d("BusStop", value);
                            addBusStopViewModel.updateSelectedBusStop(key.toInt(), value);
                            Log.d("BusStop", addBusStopViewModel.selectedBusStop.toString());
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)

                    ) {
                        Text(
                            text = key,
                            modifier = Modifier
                                .padding(8.dp),
                            style = typography.bodyLarge.copy(fontWeight = Bold)
                        )
                        Text(text = value, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }

    }
    Button(
        onClick = {
            userViewModel.editUserById(busStop = addBusStopViewModel.selectedBusStop);
            addBusStopViewModel.updateSelectedBusStop(-1, "")
            navController.navigate("Home")
                  },
        modifier = Modifier.fillMaxWidth(),
        enabled = addBusStopViewModel.selectedBusStop.id != -1
    ) {
        Text(text = "Done")
    }
}