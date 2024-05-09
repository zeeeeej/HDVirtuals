package com.yunext.virtuals.data

import androidx.compose.runtime.Stable
import com.yunext.kmp.db.entity.LogEntity
import com.yunext.virtuals.ui.screen.logger.data.TimeSetter
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
@Stable
sealed class Log(
    val id: Long,
    val timestamp: Long,
    val deviceId: String,
    val clientId: String,
    val type: String,
) {
    abstract val display: String

    companion object {
        const val UP = "LogEntity.UP"
        const val DOWN = "LogEntity.DOWN"
        const val ONLINE = "LogEntity.ONLINE"
    }
}



fun LogEntity.convert() = when (this.type) {
    LogEntity.Type.Up -> {
        UpLog(
            id = this.lid,
            timestamp = this.timestamp,
            deviceId = this.deviceId,
            clientId = this.clientId,
            topic = this.topic,
            cmd = this.cmd,
            payload = this.payload,
            state = this.state
        )
    }

    LogEntity.Type.Down -> {
        DownLog(
            id = this.lid,
            timestamp = this.timestamp,
            deviceId = this.deviceId,
            clientId = this.clientId,
            topic = this.topic,
            cmd = this.cmd,
            payload = this.payload,
        )
    }

    LogEntity.Type.Online -> {

        OnlineLog(
            id = this.lid,
            timestamp = this.timestamp,
            deviceId = this.deviceId,
            clientId = this.clientId,
            onLine = this.onLine
        )
    }

    else -> throw IllegalStateException("不支持的Log类型${this.type}")

}

fun Log.convert() = this.let { log ->
    when (log) {
        is DownLog -> {
            LogEntity(
                timestamp = log.timestamp,
                deviceId = log.deviceId,
                clientId = log.clientId,
                type = LogEntity.Type.Down,
                topic = log.topic,
                cmd = log.cmd,
                payload = log.payload,
                state = false,
                onLine = false,
            )
        }

        is OnlineLog -> {
            LogEntity(
                timestamp = log.timestamp,
                deviceId = log.deviceId,
                clientId = log.clientId,
                type = LogEntity.Type.Online,
                topic = "",
                cmd = "",
                payload = "",
                state = false,
                onLine = log.onLine,
            )
        }

        is UpLog -> {
            LogEntity(
                timestamp = log.timestamp,
                deviceId = log.deviceId,
                clientId = log.clientId,
                type = LogEntity.Type.Up,
                topic = log.topic,
                cmd = log.cmd,
                payload = log.payload,
                state = log.state,
                onLine = false,
            )
        }
    }
}

class UpLog(
    id: Long = 0,
    timestamp: Long, deviceId: String, clientId: String,
    val topic: String,
    val cmd: String,
    val payload: String,
    val state: Boolean,
) : Log(id, timestamp, deviceId, clientId, UP) {
    override val display: String
        get() = "[$timestamp] _$deviceId _$type _${topic} $cmd _$payload _$state"
}

class DownLog(
    id: Long = 0,
    timestamp: Long, deviceId: String, clientId: String,
    val topic: String,
    val cmd: String,
    val payload: String,
) : Log(id, timestamp, deviceId, clientId, DOWN) {
    override val display: String
        get() = "[$timestamp] _$deviceId _$type _${topic} $cmd _$payload "
}

class OnlineLog(
    id: Long = 0,
    timestamp: Long, deviceId: String, clientId: String,
    val onLine: Boolean,
) : Log(id, timestamp, deviceId, clientId, ONLINE) {
    override val display: String
        get() = "[$timestamp] _$deviceId _$type _$onLine"
}






