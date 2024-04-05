package com.example.stopwatch

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class StopwatchState(
    var isRunning: Boolean = false,
    var elapsedTime: Long = 0L,
    var lapCount: Int = 0
)

class StopwatchViewModel : ViewModel() {
    var state = mutableStateOf(StopwatchState())

    private var handler: Handler = Handler(Looper.getMainLooper())

    private var lastStartTime: Long = 0L // 上一次开始计时的时刻
    private var lastPauseElapsedTime: Long = 0L // 上一次 pause 为止计时的时间
    private var lastLapElapsedTime: Long = 0L // 上一个 lap 为止计时的时间

    var lapTimeList = mutableStateListOf<Long>()

    private fun getCurLapTime(): Long {
        return (lastPauseElapsedTime + SystemClock.uptimeMillis() - lastStartTime) - lastLapElapsedTime
    }

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

    fun start() {
        // 记录开始时刻
        lastStartTime = SystemClock.uptimeMillis()
        // 设置 state 为开始运行
        state.value = state.value.copy(isRunning = true)
        // 如果是首次开始, 添加 lap 1
        if (state.value.lapCount == 0) {
            lapTimeList.add(0L)
            state.value = state.value.copy(lapCount = state.value.lapCount + 1)
        }
        // 开始调用 runnable 来更新 state 时间
        handler.postDelayed(runnable, 0)
    }

    fun pause() {
        // 记录本次暂停已经过的时间, 更新 state 时间并设为停止运行
        lastPauseElapsedTime += SystemClock.uptimeMillis() - lastStartTime
        state.value = state.value.copy(isRunning = false, elapsedTime = lastPauseElapsedTime)
        // 停止调用 runnable
        handler.removeCallbacks(runnable)
    }

    fun lap() {
        // 将当前 lap 的时间记录到 list 中
        val curLapTime = getCurLapTime()
        lapTimeList.add(curLapTime)
        // 更新上个 lap 为止经过的时间
        lastLapElapsedTime += curLapTime
        //TODO 在这里更新 state, 还是交给 runnable?
        state.value = state.value.copy(elapsedTime = lastLapElapsedTime, lapCount = state.value.lapCount + 1)
    }

    fun reset() {
        // 重置 state
        state.value = StopwatchState()
        lastStartTime = 0L
        lastPauseElapsedTime = 0L
        lastLapElapsedTime = 0L
        lapTimeList.clear()
        // 只有在暂停时才能 reset, 所以不需要 removeCallbacks
        //TODO lap
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(runnable) //XXX 也许不需要
    }
}