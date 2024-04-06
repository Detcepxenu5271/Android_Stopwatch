package com.example.stopwatch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.stopwatch.data.UserPreferencesRepository

private const val STOPWATCH_DATA_NAME = "stopwatch_data"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = STOPWATCH_DATA_NAME
)

class StopwatchApplication: Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}