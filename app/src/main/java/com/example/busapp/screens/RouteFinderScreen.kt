import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.busapp.models.Leg
import com.example.busapp.models.Route
import com.example.busapp.models.TransitDetails
import com.example.busapp.models.TransitRoutesResponse
import com.example.busapp.viewmodels.RouteFinderViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFinder(navController: NavController, routeFinderViewModel: RouteFinderViewModel) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var transitRoutes by rememberSaveable { mutableStateOf(TransitRoutesResponse(emptyList())) }
    var startPredictions by rememberSaveable { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var destinationPredictions by rememberSaveable { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    if (showDialog) {
        AdvancedTimePicker(
            onConfirm = { showDialog = false },
            onDismiss = { showDialog = false },
            routeFinderViewModel
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Route Finder", fontSize = 24.sp)

        Spacer(modifier = Modifier.size(24.dp))

        LocationSearchBar(
            onClearQuery = { routeFinderViewModel.updateStartLocation("") },
            predictions = startPredictions,
            onQueryChange = {
                routeFinderViewModel.findAutocompletePredictions(it) { result ->
                    startPredictions = result
                }
            },
            placeholderText = "Choose start location",
            onSelectPrediction = { routeFinderViewModel.updateStartLocation(it) }
        )

        Spacer(modifier = Modifier.size(12.dp))

        LocationSearchBar(
            onClearQuery = { routeFinderViewModel.updateDestination("") },
            predictions = destinationPredictions,
            onQueryChange = {
                routeFinderViewModel.findAutocompletePredictions(it) { result ->
                    destinationPredictions = result
                }
            },
            placeholderText = "Choose destination",
            onSelectPrediction = { routeFinderViewModel.updateDestination(it) }
        )

        Spacer(modifier = Modifier.size(12.dp))

        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            OutlinedButton(
                enabled = false,
                onClick = {
                        if (routeFinderViewModel.travelTimeOption == "Arrive by")
                            routeFinderViewModel.updateTravelTimeOption("Leave by")
                        else
                            routeFinderViewModel.updateTravelTimeOption("Arrive by")
              },
                modifier = Modifier.width(125.dp)
            ) {
                Text(routeFinderViewModel.travelTimeOption)
            }

            val timeFormat = SimpleDateFormat("d MMM, h:mm a", Locale.getDefault())

            OutlinedButton(
                modifier = Modifier .padding(horizontal = 6.dp),
                onClick = { showDialog = true }
            ) {
                Text(timeFormat.format(routeFinderViewModel.calendar.time))
            }

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        transitRoutes = routeFinderViewModel.getRoutes()
                        println(transitRoutes.toString())
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit Icon"
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        TransitRoutesView(transitRoutes)
    }
}

@Composable
fun TransitRoutesView(transitRoutesResponse: TransitRoutesResponse) {
    if (transitRoutesResponse.routes.isEmpty()) {
        Text(text = "No routes available")
    }

    LazyColumn {
        items(transitRoutesResponse.routes) { route ->
            RouteCard(route)
        }
    }
}

@Composable
fun RouteCard(route: Route) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            route.legs.forEach { leg ->
                LegView(leg)
            }
        }
    }
}

@Composable
fun LegView(leg: Leg) {
    leg.steps.forEach { step ->
        step.transitDetails?.let { transitDetails ->
            StepView(transitDetails)
        }
    }
}

@Composable
fun StepView(transitDetails: TransitDetails) {
    Column(modifier = Modifier.padding(6.dp)) {
        Text(text = "Walk to stop:", color = Color.Black)
        Text(text = transitDetails.stopDetails.departureStop.name)

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = "Take the bus line:", color = Color.Black)
        Text(text = transitDetails.transitLine.name, color = Color(parseColor(transitDetails.transitLine.color)))

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = "Direction:", color = Color.Black)
        Text(text = transitDetails.headsign)

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = "The bus arrives at:", color = Color.Black)
        Text(text = transitDetails.localizedValues.departureTime.time.text)

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = "Exit bus at stop:", color = Color.Black)
        Text(text = transitDetails.stopDetails.arrivalStop.name)

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = "Arrive at:", color = Color.Black)
        Text(text = transitDetails.localizedValues.arrivalTime.time.text)

        Spacer(modifier = Modifier.size(6.dp))

        HorizontalDivider(modifier = Modifier.padding(3.dp), thickness = 2.dp, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchBar(
    onClearQuery: () -> Unit,
    predictions: List<AutocompletePrediction>,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    onSelectPrediction: (String) -> Unit
) {
    var active by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    DockedSearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = query,
        onQueryChange = {
            query = it
            if (it.isEmpty()) {
                query = ""
            }
            onQueryChange(it)
        },
        onSearch = {
            active = false
        },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = {
            Text(text = placeholderText)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
        },
        trailingIcon = {
            if(active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (query.isNotEmpty()) {
                            query = ""
                            onClearQuery()
                        } else {
                            active = false
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon"
                )
            }
        }
    ) {
        predictions.forEach { prediction ->
            val predictionText = prediction.getFullText(null).toString()
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .clickable {
                        query = predictionText
                        onSelectPrediction(predictionText)
                        active = false
                    }
            ) {
                Text(text = predictionText)
            }
        }
    }
}

/**
 * Advanced Time Picker Dialog taken from developer.android.com
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTimePicker(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
    routeFinderViewModel: RouteFinderViewModel
) {
    val calendar by rememberSaveable {
        mutableStateOf(Calendar.getInstance())
    }

    val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())

    var dateText by rememberSaveable {
        mutableStateOf(dateFormat.format(calendar.time))
    }

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false,
    )

    AdvancedTimePickerDialog(
        onDismiss = {
            onDismiss()
        },
        onConfirm = {
            onConfirm(timePickerState)
            val hour = timePickerState.hour
            val minute = timePickerState.minute

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            routeFinderViewModel.updateCalendar(calendar)
        },
    ) {
        TimePicker(
            state = timePickerState,
        )

        Row(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedButton(
                onClick = {
                    calendar.add(Calendar.DATE, -1)
                    dateText = dateFormat.format(calendar.time)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }

            Text(
                text = dateText,
                modifier = Modifier
                    .weight(2f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            OutlinedButton(
                onClick = {
                    calendar.add(Calendar.DATE, 1)
                    dateText = dateFormat.format(calendar.time)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "")
            }
        }

        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
fun AdvancedTimePickerDialog(
    title: String = "Select Time & Date",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
            Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}