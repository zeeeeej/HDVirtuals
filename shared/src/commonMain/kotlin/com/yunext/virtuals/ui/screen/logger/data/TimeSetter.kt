package com.yunext.virtuals.ui.screen.logger.data

import com.yunext.kmp.common.util.datetimeFormat
import com.yunext.virtuals.data.Log
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime


internal typealias TimeSetter = Long
fun TimeSetter(time:Long) = time
//internal data class TimeSetter(
//    val year: Int,
//    val month: Int,
//    val day: Int,
//    val hour: Int,
//    val minute: Int,
//    val second: Int,
//) {
//    companion object {
//        fun from(time: Long): TimeSetter = ZERO
//    }
//}

internal const val ZERO: TimeSetter = 0L
internal fun TimeSetter.isZero() = this == ZERO

internal val TimeSetter.show: String
    get() = datetimeFormat {
        this@show.toStr("yyyy-MM-dd HH:mm")
    }
//    get() = "${year.formatStr4()}-${month.formatStr2()}-${day.formatStr2()} ${hour.formatStr2()}:${minute.formatStr2()}:${second.formatStr2()}"

internal fun TimeSetter.checkShow(default: String): String =
    if (this == ZERO) default else this.show

//private fun Int.formatStr2(): String {
//    return when {
//        this <= 0 -> "00"
//        (this < 10) and (this > 0) -> "0${this}"
//        this < 100 -> "$this"
//        else -> throw IllegalStateException("$this >100")
//    }
//}

//private fun Int.formatStr4(): String {
//    return when {
//        this <= 0 -> "0000"
//        (this < 10) and (this > 0) -> "000${this}"
//        this < 100 -> "00$this"
//        this < 1000 -> "0$this"
//        this < 10000 -> "$this"
//        else -> throw IllegalStateException("$this >10000")
//    }
//}

internal enum class TimeSetterType {
    Start, End;
}

internal val Log.timestampStr: String
    //    get() = hdFormatDateTime(this.timestamp).show
    get() = this.timestamp.show

//internal fun hdFormatDateTime(dateTimeInMillis: Long): TimeSetter {
//    // 假设有一个时间戳，例如，从 Unix 纪元开始的秒数
//    val timestamp: Long = 1631104000 // 2021-09-01 12:00:00 UTC
//
//    // 将时间戳转换为 Instant 对象
//    val instant = Instant.fromEpochMilliseconds(dateTimeInMillis)
//
//    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
//    return TimeSetter(
//        year = localDateTime.year,
//        month = localDateTime.month.number,
//        day = localDateTime.dayOfMonth,
//        hour = localDateTime.hour,
//        localDateTime.minute,
//        localDateTime.second
//    )
//
//
//}