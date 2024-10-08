package com.example.busapp.places

import android.util.Log
import com.example.busapp.BuildConfig
import com.example.busapp.routes.RoutesRepository
import com.example.busapp.routes.RoutesRepositoryImpl
import com.example.busapp.viewmodels.RouteFinderViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@FlowPreview
val placesModule = module {
    single<PlacesClient> {
        // Define a variable to hold the Places API key.
        val apiKey = BuildConfig.MAPS_API_KEY

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(androidContext(), apiKey)
        Log.d("Places test", "Places SDK initialized successfully.")

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(androidContext())
        Log.d("Places test", "PlacesClient created successfully.")

        placesClient
    }

    single<PlacesRepository> {
        PlacesRepositoryImpl(get())
    }

    single<RoutesRepository> {
        RoutesRepositoryImpl()
    }

    viewModel {
        RouteFinderViewModel(get(), get())
    }
}