package com.example.assignment2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment2.ui.theme.Assignment2Theme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlightTrackerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FlightTrackerScreen(
    viewModel: FlightViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val flightState by viewModel.flightState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isTracking by remember { mutableStateOf(false) }
    var flightNumber by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Flight Tracker",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Flight number input with helper text
        OutlinedTextField(
            value = flightNumber,
            onValueChange = { flightNumber = it },
            label = { Text("Flight Number (e.g., BA123)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (validateFlightNumber(flightNumber)) {
                        viewModel.getFlightDetails(flightNumber)
                    } else {
                        Toast.makeText(context, "Please enter a valid flight number", Toast.LENGTH_SHORT).show()
                    }
                }
            ),
            supportingText = { Text("Enter airline code + flight number (e.g., BA123, LH456)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Track/Stop button
        Button(
            onClick = {
                if (validateFlightNumber(flightNumber)) {
                    if (!isTracking) {
                        // Start tracking
                        isTracking = true
                        coroutineScope.launch {
                            viewModel.startTracking(flightNumber)
                        }
                        Toast.makeText(context, "Tracking started for $flightNumber", Toast.LENGTH_SHORT).show()
                    } else {
                        // Stop tracking
                        isTracking = false
                        viewModel.stopTracking()
                        Toast.makeText(context, "Tracking stopped", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter a valid flight number", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isTracking) "Stop Tracking" else "Start Tracking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Flight information display
        when (flightState) {
            is FlightState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is FlightState.Success -> {
                val flightData = (flightState as FlightState.Success).flightData
                FlightInfoCard(flightData)
                ApiSourceInfo()
            }
            is FlightState.Error -> {
                val errorMessage = (flightState as FlightState.Error).message
                ErrorMessage(errorMessage)
            }
            is FlightState.Initial -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Enter a flight number to start tracking")
                }
            }
        }
    }
}

@Composable
fun FlightInfoCard(flightData: FlightData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Flight ${flightData.flightNumber}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("From: ${flightData.departureAirport}")
                    Text("To: ${flightData.arrivalAirport}")
                }
                Column {
                    Text("Status: ${flightData.status}")
                    Text("Updated: ${formatDateTime(flightData.lastUpdated)}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Flight progress
            Text("Flight Progress", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = flightData.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(flightData.departureAirport)
                Text(flightData.arrivalAirport)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current location details
            Text("Current Location", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("Altitude", "${flightData.altitude} ft")
                InfoItem("Speed", "${flightData.speed} km/h")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("Latitude", flightData.latitude.toString())
                InfoItem("Longitude", flightData.longitude.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional information
            Text("Flight Information", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("Aircraft", flightData.aircraft)
                InfoItem("ETA", formatTime(flightData.estimatedArrival))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("Departure", formatTime(flightData.scheduledDeparture))
                InfoItem("Time Left", flightData.timeRemaining)
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp)
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFFFEBEE))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Red,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ApiSourceInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Data source: OpenSky Network API",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = "Flight data updates every minute",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlightTrackerPreview() {
    Assignment2Theme {
        FlightTrackerScreen()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Assignment2Theme {
        Greeting("Android")
    }
}

// Helper functions
fun validateFlightNumber(flightNumber: String): Boolean {
    // Basic validation: Check if it's not empty and follows a general pattern
    // Most flight numbers are 2-3 letter airline code followed by 1-4 digits
    return flightNumber.isNotEmpty() &&
            flightNumber.length >= 3 &&
            flightNumber.length <= 7 &&
            flightNumber.substring(0, 2).all { it.isLetter() } &&
            flightNumber.substring(2).any { it.isDigit() }
}

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

