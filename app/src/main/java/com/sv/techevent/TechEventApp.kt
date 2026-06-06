package com.sv.techevent

import android.app.Application
import com.sv.techevent.di.AppContainer

class TechEventApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}