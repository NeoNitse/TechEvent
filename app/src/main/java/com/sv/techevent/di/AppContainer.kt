package com.sv.techevent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.sv.techevent.data.local.AppDatabase
import com.sv.techevent.data.remote.RetrofitInstance
import com.sv.techevent.data.repository.EventRepository

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class AppContainer(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "techevent_db"
    ).fallbackToDestructiveMigration(true).build()

    private val eventDao = database.eventDao()
    private val apiService = RetrofitInstance.api

    val eventRepository: EventRepository = EventRepository(
        apiService = apiService,
        eventDao = eventDao
    )

    val dataStore: DataStore<Preferences> = context.dataStore
}