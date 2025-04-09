data class CachedFlightInfo(
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: Long,
    val arrivalTime: Long,
    val firstSeenAt: Long
)

data class FlightData(
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val scheduledDeparture: Long,
    val estimatedArrival: Long,
    val status: String,
    val aircraft: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Int,
    val speed: Int,
    val progress: Float,
    val timeRemaining: String,
    val lastUpdated: Long
)