package com.example.busapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.busapp.models.BusRoute
import com.example.busapp.models.BusStop
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bus_data")

@FlowPreview
val dataAccessModule = module {
    single<Storage<BusStop>> {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<BusStop>>(){}.type,
            preferenceKey = stringPreferencesKey("busstop"),
            dataStore = androidContext().dataStore
        )
    }
    single<Storage<BusRoute>> {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<BusRoute>>(){}.type,
            preferenceKey = stringPreferencesKey("busroute"),
            dataStore = androidContext().dataStore
        )
    }

    single { Gson() }

}