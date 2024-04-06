package com.example.stopwatch

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.stopwatch.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Stopwatch 的状态, 用于直接更新 UI (除了 lap 列表)
 *
 * @property isRunning
 * @property elapsedTime
 */
data class StopwatchState(
    var isRunning: Boolean = false,
    var elapsedTime: Long = 0L,
)

/**
 * Stopwatch 的 ViewModel
 *
 * @property userPreferencesRepository
 */
class StopwatchViewModel (
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var state = mutableStateOf(StopwatchState())

    private var handler: Handler = Handler(Looper.getMainLooper())

    private var lastStartTime: Long = 0L // 上一次开始计时的时刻
    private var lastPauseElapsedTime: Long = 0L // 上一次 pause 为止计时的时间
    private var lastLapElapsedTime: Long = 0L // 上一个 lap 为止计时的时间

    var lapTimeList = mutableStateListOf<Long>()

    // 初始化 userPreferencesRepository, 使用 Preferences Datastore 进行持久化
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as StopwatchApplication)
                StopwatchViewModel(application.userPreferencesRepository)
            }
        }
    }

    // 通过读取 userPreferencesRepository 初始化相关数据
    init {
        CoroutineScope(Dispatchers.Main).launch {
            // 从 userPreferencesRepository 读取持久化的数据
            val isRunning = userPreferencesRepository.isRunning.first()
            lastStartTime = userPreferencesRepository.lastStartTime.first()
            lastPauseElapsedTime = userPreferencesRepository.lastPauseElapsedTime.first()
            lastLapElapsedTime = userPreferencesRepository.lastLapElapsedTime.first()
            lapTimeList.addAll(userPreferencesRepository.lapTimeList.first())

            // 更新 isRunning 状态, 计算当前时间
            if (isRunning) {
                state.value = state.value.copy(isRunning, lastPauseElapsedTime + SystemClock.uptimeMillis() - lastStartTime)
                // 启动秒表的更新
                handler.postDelayed(runnable, 0)
            } else {
                state.value = state.value.copy(isRunning, lastPauseElapsedTime)
            }
        }
    }

    // 持久化相关数据到 userPreferencesRepository
    private fun persistStopwatchData(
        isRunning: Boolean? = null,
        lastStartTime: Long? = null,
        lastPauseElapsedTime: Long? = null,
        lastLapElapsedTime: Long? = null,
        lapTimeList: List<Long>? = null
    ) {
        viewModelScope.launch {
            userPreferencesRepository.saveStopwatchData(isRunning, lastStartTime, lastPauseElapsedTime, lastLapElapsedTime, lapTimeList)
        }
    }

    // 获取当前 lap 的计时
    private fun getCurLapTime(): Long {
        return (lastPauseElapsedTime + SystemClock.uptimeMillis() - lastStartTime) - lastLapElapsedTime
    }

    // 更新计时数据, 通过 state 更新 UI
    private val runnable = object : Runnable {
        override fun run() {
            // 更新 state 时间和当前 lap 时间
            val newElapsedTime = lastPauseElapsedTime + SystemClock.uptimeMillis() - lastStartTime
            lapTimeList[lapTimeList.lastIndex] = newElapsedTime - lastLapElapsedTime
            state.value = state.value.copy(elapsedTime = newElapsedTime)
            // 继续调用 run
            handler.postDelayed(this, 0) //TODO dealy 一点时间?
        }
    }

    // 开始/继续计时
    fun start() {
        // 如果是首次开始, 添加 lap 1
        if (lastStartTime == 0L) {
            lapTimeList.add(0L)
            // 持久化数据
            persistStopwatchData(lapTimeList = lapTimeList)
        }
        // 记录开始时刻
        lastStartTime = SystemClock.uptimeMillis()
        // 设置 state 为开始运行
        state.value = state.value.copy(isRunning = true)
        // 持久化数据
        persistStopwatchData(isRunning = state.value.isRunning, lastStartTime = lastStartTime)
        // 开始调用 runnable 来更新 state 时间
        handler.postDelayed(runnable, 0)
    }

    // 暂停计时
    fun pause() {
        // 记录本次暂停已经过的时间, 更新 state 时间并设为停止运行
        lastPauseElapsedTime += SystemClock.uptimeMillis() - lastStartTime
        state.value = state.value.copy(isRunning = false, elapsedTime = lastPauseElapsedTime)
        // 持久化数据
        persistStopwatchData(isRunning = state.value.isRunning, lastPauseElapsedTime = lastPauseElapsedTime)
        // 停止调用 runnable
        handler.removeCallbacks(runnable)
    }

    // 记录 lap
    fun lap() {
        // 将当前 lap 的时间记录到 list 中
        val curLapTime = getCurLapTime()
        lapTimeList.add(curLapTime)
        // 更新上个 lap 为止经过的时间
        lastLapElapsedTime += curLapTime
        // 持久化数据
        persistStopwatchData(lastLapElapsedTime = lastLapElapsedTime, lapTimeList = lapTimeList)
        //TODO 在这里更新 state, 还是交给 runnable?
        state.value = state.value.copy(elapsedTime = lastLapElapsedTime)
    }

    // 重置计时和 lap
    fun reset() {
        // 重置 state
        state.value = StopwatchState()
        lastStartTime = 0L
        lastPauseElapsedTime = 0L
        lastLapElapsedTime = 0L
        lapTimeList.clear()
        // 持久化数据
        persistStopwatchData(state.value.isRunning, lastStartTime, lastPauseElapsedTime, lastLapElapsedTime, lapTimeList)
        // 只有在暂停时才能 reset, 所以不需要 removeCallbacks
        //TODO lap
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(runnable) //XXX 也许不需要
    }
}