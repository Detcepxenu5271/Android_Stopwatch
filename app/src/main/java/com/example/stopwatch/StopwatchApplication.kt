// 参考 https://developer.android.google.cn/codelabs/basic-android-kotlin-compose-datastore?continue=https%3A%2F%2Fdeveloper.android.google.cn%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-6-pathway-3%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-datastore#4
package com.example.stopwatch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.stopwatch.data.UserPreferencesRepository

// 创建 Preferences DataStore
private const val STOPWATCH_DATA_NAME = "stopwatch_data"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = STOPWATCH_DATA_NAME
)

/**
 * Stopwatch 应用
 *
 * 应用启动时, 首先创建 StopwatchApplication, 在 onCreate 中进行 UserPreferencesRepository 的依赖注入
 */
class StopwatchApplication: Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}