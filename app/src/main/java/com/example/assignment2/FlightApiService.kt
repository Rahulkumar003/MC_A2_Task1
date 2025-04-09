// FlightApiService.kt
package com.example.assignment2

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenSkyApiService {
    // Get all states (flights) in the specified bounding box
    @GET("states/all")
    suspend fun getAllStates(
        @Query("lamin") lamin: Double? = null,
        @Query("lomin") lomin: Double? = null,
        @Query("lamax") lamax: Double? = null,
        @Query("lomax") lomax: Double? = null,
        @Query("time") time: Long? = null
    ): OpenSkyResponse

    // Get states (flights) by aircraft
    @GET("states/all")
    suspend fun getStatesByAircraft(
        @Query("icao24") icao24: String,
        @Query("time") time: Long? = null
    ): OpenSkyResponse
}

// Data models for OpenSky API response
data class OpenSkyResponse(
    val time: Long = 0,
    val states: List<List<Any?>>? = null
)