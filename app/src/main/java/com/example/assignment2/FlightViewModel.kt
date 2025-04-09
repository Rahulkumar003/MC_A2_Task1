// FlightViewModel.kt
package com.example.assignment2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class FlightViewModel : ViewModel() {
    private val _flightState = MutableStateFlow<FlightState>(FlightState.Initial)
    val flightState: StateFlow<FlightState> = _flightState

    private val repository: FlightRepository = FlightRepository()
    private var trackingJob: Job? = null

    fun getFlightDetails(flightNumber: String) {
        viewModelScope.launch {
            _flightState.value = FlightState.Loading

            try {
                val response = repository.getFlightInfo(flightNumber)
                _flightState.value = FlightState.Success(response)
            } catch (e: HttpException) {
                _flightState.value = FlightState.Error("Network error: ${e.message ?: "Unknown error"}")
            } catch (e: IOException) {
                _flightState.value = FlightState.Error("Connection error: ${e.message ?: "Check your internet connection"}")
            } catch (e: Exception) {
                _flightState.value = FlightState.Error("Error: ${e.message ?: "Unknown error occurred"}")
            }
        }
    }

    fun startTracking(flightNumber: String) {
        stopTracking()

        trackingJob = viewModelScope.launch {
            while (true) {
                try {
                    getFlightDetails(flightNumber)
                } catch (e: Exception) {
                    // Log error but don't stop tracking
                    _flightState.value = FlightState.Error("Tracking error: ${e.message}. Will retry...")
                    delay(5000) // Wait 5 seconds before retrying after error
                    continue
                }
                delay(60000) // Update every minute
            }
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}

sealed class FlightState {
    object Initial : FlightState()
    object Loading : FlightState()
    data class Success(val flightData: FlightData) : FlightState()
    data class Error(val message: String) : FlightState()
}