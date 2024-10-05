import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.busapp.viewmodels.RouteFinderViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFinder(navController: NavController, routeFinderViewModel: RouteFinderViewModel) {
    var checked by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Route Finder", fontSize = 24.sp)

        Spacer(modifier = Modifier.size(24.dp))

        LocationSelector(routeFinderViewModel, isStartLocation = true)

        Spacer(modifier = Modifier.size(12.dp))

        LocationSelector(routeFinderViewModel, isStartLocation = false)

        Spacer(modifier = Modifier.size(12.dp))

        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp)
        ) {
            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(end = 12.dp)
            ) {
                val timeOptionMsg = if (checked) {
                    "Arrive by"
                } else {
                    "Leave by"
                }

                Text(timeOptionMsg)
            }

            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                }
            )
        }

        if (showDialog) {
            AdvancedTimePicker(
                onConfirm = { showDialog = false },
                onDismiss = { showDialog = false }
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        Text(text = "Upcoming", fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelector(routeFinderViewModel: RouteFinderViewModel, isStartLocation: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        val currentLocation = if (isStartLocation) {
            routeFinderViewModel.startLocation
        } else {
            routeFinderViewModel.destination
        }

        OutlinedTextField(
            value = TextFieldValue(currentLocation, selection = TextRange(currentLocation.length)),
            onValueChange = {
                if (isStartLocation) {
                    routeFinderViewModel.updateStartLocation(it.text)
                } else {
                    routeFinderViewModel.updateDestination(it.text)
                }

                routeFinderViewModel.findAutocompletePredictions(it.text) { result ->

                    predictions = if (it.text.isEmpty()) {
                        emptyList()
                    } else { result }

                    expanded = result.isNotEmpty() && it.text.isNotEmpty()
                }
            },
            modifier = Modifier
                .menuAnchor(),
            label = {
                if (isStartLocation) {
                    Text("Choose start location")
                } else {
                    Text("Choose destination")
                }
            },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 150.dp)
        ) {
            predictions.forEach { prediction ->
                val predictionText = prediction.getPrimaryText(null).toString()

                DropdownMenuItem(
                    text = { Text(predictionText, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        if (isStartLocation) {
                            routeFinderViewModel.updateStartLocation(predictionText)
                        } else {
                            routeFinderViewModel.updateDestination(predictionText)
                        }

                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

// Code snippet base taken from developer.android.com
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTimePicker(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )

    AdvancedTimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) },
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
fun AdvancedTimePickerDialog(
    title: String = "Select Time",
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
                modifier = Modifier.padding(24.dp),
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
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }

                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("d MMMM")
                    val selectedDay = dateFormat.format(calendar.time)

                    Text(
                        text = selectedDay,
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    OutlinedButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "")
                    }
                }

                Spacer(modifier = Modifier.size(24.dp))

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