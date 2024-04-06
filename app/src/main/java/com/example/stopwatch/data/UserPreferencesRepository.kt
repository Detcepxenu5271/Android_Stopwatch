package com.example.stopwatch.data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.stopwatch.TimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_RUNNING = booleanPreferencesKey("is_running")
        val LAST_START_TIME = longPreferencesKey("last_start_time")
        val LAST_PAUSE_ELAPSED_TIME = longPreferencesKey("last_pause_elapsed_time")
        val LAST_LAP_ELAPSED_TIME = longPreferencesKey("last_lap_elapsed_time")
        val LAST_TIME_LIST = stringPreferencesKey("last_time_list")
        const val TAG = "UserPreferencesRepo"
    }

    val isRunning: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences[IS_RUNNING].", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_RUNNING] ?: false
        }

    val lastStartTime: Flow<Long> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences[LAST_START_TIME].", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_START_TIME] ?: 0L
        }

    val lastPauseElapsedTime: Flow<Long> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences[LAST_PAUSE_ELAPSED_TIME].", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_PAUSE_ELAPSED_TIME] ?: 0L
        }

    val lastLapElapsedTime: Flow<Long> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences[LAST_LAP_ELAPSED_TIME].", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_LAP_ELAPSED_TIME] ?: 0L
        }

    val lapTimeList: Flow<List<Long>> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences[LAST_TIME_LIST].", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            // preferences[LAST_TIME_LIST] 可能为空, 因此 TimeUtils 中的函数需要特殊处理
            //TODO 有些耦合
            TimeUtils.stringToListLong(preferences[LAST_TIME_LIST]) ?: listOf<Long>()
        }

    suspend fun saveStopwatchData(
        isRunning: Boolean? = null,
        lastStartTime: Long? = null,
        lastPauseElapsedTime: Long? = null,
        lastLapElapsedTime: Long? = null,
        lapTimeList: List<Long>? = null
        ) {
        dataStore.edit { preferences ->
            isRunning?.let { preferences[IS_RUNNING] = it }
            lastStartTime?.let { preferences[LAST_START_TIME] = it }
            lastPauseElapsedTime?.let { preferences[LAST_PAUSE_ELAPSED_TIME] = it }
            lastLapElapsedTime?.let { preferences[LAST_LAP_ELAPSED_TIME] = it }
            lapTimeList?.let { preferences[LAST_TIME_LIST] = TimeUtils.listLongToString(it) }
        }
    }
}