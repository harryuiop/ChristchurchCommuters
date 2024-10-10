package com.example.busapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.BusAlert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Search
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.busapp.models.BusStop
import com.example.busapp.ui.theme.Purple80
import com.example.busapp.ui.theme.PurpleGrey40
import com.example.busapp.ui.theme.PurpleGrey80
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
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(text = "Metro Bus Stops", fontSize = 24.sp)
        OutlinedTextField(
            value = addBusStopViewModel.userQuery,
            onValueChange = { newQuery -> addBusStopViewModel.updateQuery(newQuery) },
            label = { Text("Search") },
            placeholder = { Text("Type Stop # or Location") },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(busStops.entries.toList()) { index, (key, value) ->
                if (value.contains(
                        addBusStopViewModel.userQuery,
                        ignoreCase = true
                    ) || key.contains(addBusStopViewModel.userQuery, ignoreCase = true)
                ) {
                    OutlinedCard(
                        onClick = {
                            addBusStopViewModel.updateSelectedBusStop(key.toInt(), value);
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = PurpleGrey40,
                        )
                    ) {
                        Text(
                            text = "#$key",
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                                .offset(x=(0).dp,y=(6).dp),
                            style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Text(text = value, modifier = Modifier.padding(8.dp)
                                                .offset(x=(0).dp,y=(-5).dp)
                                                .align(Alignment.CenterHorizontally), style = typography.bodyLarge.copy(fontWeight = Bold))
                    }
                }
            }
        }
            Button(
                onClick = {
                    userViewModel.editUserById(busStop = addBusStopViewModel.selectedBusStop);
                    addBusStopViewModel.updateSelectedBusStop(-1, "")
                    addBusStopViewModel.updateQuery("")
                    navController.navigate("Home")
                },
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth(),
                enabled = addBusStopViewModel.selectedBusStop.id != -1
            ) {
                Text(text = "Add to Home")
            }

        Spacer(modifier = Modifier.size(12.dp))


    }

}