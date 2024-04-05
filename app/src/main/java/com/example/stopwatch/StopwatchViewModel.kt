package com.example.stopwatch

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class StopwatchState(
    var isRunning: Boolean = false,
    var elapsedTime: Long = 0L
)

class StopwatchViewModel : ViewModel() {
    var state = mutableStateOf(StopwatchState())

    private var handler: Handler = Handler(Looper.getMainLooper())

    private var lastStartTime: Long = 0L // 上一次开始计时的时刻
    private var lastPauseElapsedTime: Long = 0L // 上一次 pause 为止计时的时间
    private var lastLapElapsedTime: Long = 0L // 上一个 lap 为止计时的时间

    private val runnable = object : Runnable {
        override fun run() {
            // 更新 state 时间
            val newElapsedTime = SystemClock.uptimeMillis() - lastStartTime
            state.value = state.value.copy(elapsedTime = lastPauseElapsedTime + newElapsedTime)
            // 继续调用 run
            handler.postDelayed(this, 0) //TODO dealy 一点时间?
        }
    }

    fun start() {
        // 记录开始时刻
        lastStartTime = SystemClock.uptimeMillis()
        // 设置 state 为开始运行
        state.value = state.value.copy(isRunning = true)
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

    fun reset() {
        // 重置 state
        state.value = StopwatchState()
        lastStartTime = 0L
        lastPauseElapsedTime = 0L
        lastLapElapsedTime = 0L
        // 只有在暂停时才能 reset, 所以不需要 removeCallbacks
        //TODO lap
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(runnable) //XXX 也许不需要
    }
}