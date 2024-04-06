package com.example.stopwatch

/**
 * 和时间数据计算有关的单例类
 *
 * 注: 使用 Long 存储以毫秒为单位的时间
 */
object TimeUtils {
    // Long 表示的时间格式化为字符串
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

    // 计算时间的秒数进度, 即占一分钟的比例
    fun minuteProgress(elapsedTime: Long): Float {
        return (elapsedTime % (60 * 1000) / 1000f) / 60f
    }

    // 时间 List 转字符串, 用于将 List 数据持久化到 Preferences DataStore
    fun listLongToString(list: List<Long>): String {
        return list.joinToString(separator = ",")
    }

    // 字符串转时间 List, 用于从 Preferences DataStore 读取持久化的 List 数据
    fun stringToListLong(str: String?): List<Long>? {
        if (str == null) return null
        return str.split(",").map { it.toLong() }
    }
}