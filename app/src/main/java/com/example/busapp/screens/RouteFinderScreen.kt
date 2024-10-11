import android.content.Intent
import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.busapp.R
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
    val timeFormat = SimpleDateFormat("d MMM, h:mm a", Locale.getDefault())

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
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.route_finder_title), fontSize = 24.sp)

        Spacer(modifier = Modifier.size(24.dp))

        LocationSearchBar(
            onClearQuery = {
                routeFinderViewModel.updateStartLocation("")
                routeFinderViewModel.updateStartPredictions(emptyList())
            },
            predictions = routeFinderViewModel.startPredictions,
            onQueryChange = {
                routeFinderViewModel.findAutocompletePredictions(it) { result ->
                    routeFinderViewModel.updateStartPredictions(result)
                }
            },
            placeholderText = stringResource(id = R.string.search_placeholder_start_location),
            onSelectPrediction = { routeFinderViewModel.updateStartLocation(it) }
        )

        Spacer(modifier = Modifier.size(12.dp))

        LocationSearchBar(
            onClearQuery = {
                routeFinderViewModel.updateDestination("")
                routeFinderViewModel.updateDestinationPredictions(emptyList())
            },
            predictions = routeFinderViewModel.destinationPredictions,
            onQueryChange = {
                routeFinderViewModel.findAutocompletePredictions(it) { result ->
                    routeFinderViewModel.updateDestinationPredictions(result)
                }
            },
            placeholderText = stringResource(id = R.string.search_placeholder_destination),
            onSelectPrediction = { routeFinderViewModel.updateDestination(it) }
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.button_depart_at),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { showDialog = true }
            ) {
                Text(timeFormat.format(routeFinderViewModel.calendar.time))
            }

            OutlinedButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        routeFinderViewModel.fetchRoutes()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFFD0BCFF))
                ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.button_submit_routes_request),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        TransitRoutesView(routeFinderViewModel.transitRoutes)
    }
}

@Composable
fun TransitRoutesView(transitRoutesResponse: TransitRoutesResponse) {
    if (transitRoutesResponse.routes.isNullOrEmpty()) {
        Text(text = stringResource(id = R.string.no_routes_available))
    } else {
        Column {
            transitRoutesResponse.routes.forEach() { route ->
                RouteCard(route)
            }
        }
    }
}

@Composable
fun RouteCard(route: Route) {
    // Ensure the route has transit details
    val hasTransitDetails = route.legs.any { leg ->
        leg.steps.any { step -> step.transitDetails != null }
    }

    val leg = route.legs.first()
    val firstStep = leg.steps.firstOrNull { it.transitDetails != null }
    val lastStep = leg.steps.lastOrNull { it.transitDetails != null }
    val stepsSize = leg.steps.filter { it.transitDetails != null }.size

    var expanded by rememberSaveable { mutableStateOf(false) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    if (hasTransitDetails) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.elevatedCardElevation(),
            onClick = {
                expanded = !expanded
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // First step departure time
                            firstStep?.transitDetails?.let { transitDetails ->
                                Text(text = transitDetails.localizedValues.departureTime.time.text)
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = stringResource(id = R.string.icon_content_desc_transfer)
                            )

                            // In-between steps (only show 2 at most)
                            if (stepsSize > 2) {
                                Icon(
                                    imageVector = Icons.Filled.MoreHoriz,
                                    contentDescription = stringResource(id = R.string.icon_content_desc_ellipsis)
                                )

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = stringResource(id = R.string.icon_content_desc_transfer)
                                )

                            } else {
                                leg.steps.forEach { step ->
                                    step.transitDetails?.let { transitDetails ->
                                        Text(
                                            text = transitDetails.transitLine.nameShort?:"?",
                                            color = Color(parseColor(transitDetails.transitLine.color))
                                        )

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = stringResource(id = R.string.icon_content_desc_transfer)
                                        )
                                    }
                                }
                            }

                            // Final step arrive time
                            lastStep?.transitDetails?.let { transitDetails ->
                                Text(text = transitDetails.localizedValues.arrivalTime.time.text)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(id = R.string.icon_content_desc_expand_collapse),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (expanded) {
                    LegView(leg)
                }
            }
        }
    }
}

@Composable
fun LegView(leg: Leg) {
    val context = LocalContext.current
    var shareRouteText = ""
    leg.steps.forEach { step ->
        step.transitDetails?.let { transitDetails ->
            val newShareRouteText = stepView(transitDetails)
            shareRouteText += newShareRouteText
        }
    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareRouteText.trim())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(context, shareIntent, null)
    }) {
        Text(text = stringResource(id = R.string.share_route))
    }
}

@Composable
fun stepView(transitDetails: TransitDetails): String {
    var currentShareRouteText = ""
    Column(modifier = Modifier.padding(6.dp)) {
        HorizontalDivider(modifier = Modifier.padding(3.dp), thickness = 2.dp, color = Color.White)

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = stringResource(id = R.string.walk_to_stop), color = Color.Gray)
        currentShareRouteText = currentShareRouteText.plus(stringResource(id = R.string.walk_to_stop) + " ")
        Text(text = transitDetails.stopDetails.departureStop.name)
        currentShareRouteText = currentShareRouteText.plus(transitDetails.stopDetails.departureStop.name + "\n")

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = stringResource(id = R.string.take_the_bus_line), color = Color.Gray)
        currentShareRouteText = currentShareRouteText.plus(stringResource(id = R.string.take_the_bus_line) + " ")
        Text(text = transitDetails.transitLine.name, color = Color(parseColor(transitDetails.transitLine.color)))
        currentShareRouteText = currentShareRouteText.plus(transitDetails.transitLine.name + "\n")

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = stringResource(id = R.string.the_bus_departs_at), color = Color.Gray)
        currentShareRouteText = currentShareRouteText.plus(stringResource(id = R.string.the_bus_departs_at) + " ")
        Text(text = transitDetails.localizedValues.departureTime.time.text)
        currentShareRouteText = currentShareRouteText.plus(transitDetails.localizedValues.departureTime.time.text + "\n")

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = stringResource(id = R.string.direction), color = Color.Gray)
        currentShareRouteText = currentShareRouteText.plus(stringResource(id = R.string.direction) + " ")
        Text(text = transitDetails.headsign)
        currentShareRouteText = currentShareRouteText.plus(transitDetails.headsign + "\n")

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = stringResource(id = R.string.exit_bus_at_stop), color = Color.Gray)
        currentShareRouteText = currentShareRouteText.plus(stringResource(id = R.string.exit_bus_at_stop) + " ")
        Text(text = transitDetails.stopDetails.arrivalStop.name)
        currentShareRouteText = currentShareRouteText.plus(transitDetails.stopDetails.arrivalStop.name + "\n")

        Spacer(modifier = Modifier.size(6.dp))

        Text(text = stringResource(id = R.string.the_bus_arrives_at), color = Color.Gray)
        currentShareRouteText = currentShareRouteText.plus(stringResource(id = R.string.the_bus_arrives_at) + " ")
        Text(text = transitDetails.localizedValues.arrivalTime.time.text)
        currentShareRouteText = currentShareRouteText.plus(transitDetails.localizedValues.arrivalTime.time.text + "\n" + "\n")
    }
    return currentShareRouteText
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
                    contentDescription = stringResource(id = R.string.icon_content_desc_close)
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.icon_content_desc_back)
                )
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.icon_content_desc_forward)
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
fun AdvancedTimePickerDialog(
    title: String = stringResource(id = R.string.time_picker_title),
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
                    TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.cancel)) }
                    TextButton(onClick = onConfirm) { Text(stringResource(id = R.string.ok)) }
                }
            }
        }
    }
}