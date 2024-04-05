package com.example.stopwatch

object TimeUtils {
    // 使用 Long 存储以毫秒为单位的时间

    fun long2String(elapsedTime: Long): String {
        var t = elapsedTime
        val milliseconds = t % 1000
        t /= 1000
        val seconds = t % 60
        t /= 60
        val minutes = t % 60
        t /= 60
        val hours = t
        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds)
    }

    fun minuteProgress(elapsedTime: Long): Float {
        return (elapsedTime % (60 * 1000) / 1000f) / 60f
    }
}