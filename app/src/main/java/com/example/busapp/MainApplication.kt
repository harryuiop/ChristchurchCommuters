package com.example.busapp

import android.app.Application
import com.example.busapp.datastore.userDataAccessModule
import com.example.busapp.datastore.stopAccessModule
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication: Application() {
    @OptIn(FlowPreview::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(userDataAccessModule, stopAccessModule)
        }
    }
}