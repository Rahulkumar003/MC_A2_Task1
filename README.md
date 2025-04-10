# Flight Tracker App

This project is a **Flight Tracker App** built using **Kotlin** and **Jetpack Compose**. It allows users to track real-time flight information, including flight progress, current location, and other details, using the OpenSky Network API.

---

## Features

- **Search Flights**: Enter a flight number (e.g., `BA123`, `LH456`) to fetch flight details.
- **Real-Time Tracking**: Start and stop tracking flights with live updates.
- **Flight Details**:
  - Departure and arrival airports.
  - Current altitude, speed, and location.
  - Flight progress and estimated time of arrival.
- **Error Handling**: Displays appropriate messages for invalid flight numbers or API errors.
- **Simulated Data**: Provides fallback simulated data when API responses are unavailable.

---

## Technologies Used

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: For building the UI.
- **Retrofit**: For API calls to the OpenSky Network.
- **Coroutines**: For asynchronous operations.
- **Gradle**: Build system.

---

## Project Structure

- `MainActivity.kt`: Contains the main UI logic and user interactions.
- `FlightRepository.kt`: Handles API calls and data processing.
- `FlightData.kt`: Data model for flight information.
- `OpenSkyApiService.kt`: Retrofit interface for OpenSky API endpoints.

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/flight-tracker.git
   cd flight-tracker
   ```

2. Open the project in **Android Studio**.

3. Sync Gradle and ensure all dependencies are installed.

4. Run the app on an emulator or physical device.

---

## API Integration

This app uses the **OpenSky Network API** to fetch flight data. Ensure you have internet access while using the app.

---

## Screenshots

- **Flight Search**: Enter a flight number to fetch details.
- **Flight Details**: View real-time flight progress and information.
- **Error Handling**: Displays error messages for invalid inputs or API issues.

---

## Future Enhancements

- Add a map view to display the flight's real-time location.
- Support for filtering flights by region or airline.
- Improved error handling and offline support.

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
