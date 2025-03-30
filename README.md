# Christchurch Commuters

## Overview
Christchurch Metro Navigator is an Android application built with Kotlin and Jetpack Compose to help residents and visitors of Christchurch locate, plan, view, and interact with the Christchurch metro system. The app provides real-time transit updates and route planning for a seamless commuting experience.

## Features
- **Real-time Metro Information**: View live updates on bus and train schedules.
- **Route Planning**: Easily find the best routes to your destination.
- **Favorite Locations**: Save frequently visited places for quick access.
- **Alerts & Notifications**: Stay informed about service disruptions or delays.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit / Ktor
- **Database**: Room Database
- **State Management**: StateFlow / LiveData

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/christchurch-metro-navigator.git
   ```
2. Open the project in Android Studio.
3. Build and run the project on an emulator or a physical device.

## API Configuration
The app fetches real-time transit data using external APIs. To configure API access:
1. Obtain an API key from the Christchurch Metro Transit provider.
2. Create a `local.properties` file in the root project directory.
3. Add the following line:
   ```
   API_KEY=your_api_key_here
   ```
4. Ensure the key is properly referenced in your app's networking layer.

## Contributing
Contributions are welcome! If you'd like to contribute:
1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Open a pull request.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


