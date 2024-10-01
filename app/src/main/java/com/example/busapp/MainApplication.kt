package com.example.busapp

import android.app.Application

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
//TODO:
//        startKoin {
//            androidContext(this@MainApplication)
//            modules(dataAccessModule)
//        }
    }
}