package com.yunext.kmp.db.entity

import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.common.util.hdUUID
import com.yunext.kmp.database.Hd_log
import kotlin.random.Random

data class LogEntity(
    val lid: Long = 0,
    val timestamp: Long,
    val deviceId: String,
    val clientId: String,
    /*类型：上行 下行 上下线*/
    val type: Type,
    val topic: String,
    val cmd: String,
    val payload: String,
    /*上行数据成功或失败*/
    val state: Boolean,
    /*上线/下线*/
    val onLine: Boolean,
) {
    enum class Type( val type: String) {
        Up(TYPE_UP), Down(TYPE_DOWN), Online(TYPE_ONLINE)
        ;

    }

    companion object {

        fun typeOf(type: String): Type {
            return when(type){
                Type.Down.type->Type.Down
                Type.Up.type->Type.Up
                Type.Online.type->Type.Online
                else->throw IllegalArgumentException("type=$type 不支持")
            }
        }

        private const val TYPE_UP = "up"
        private const val TYPE_DOWN = "down"
        private const val TYPE_ONLINE = "online"

         fun fake(id: Long) = LogEntity(
            lid = id,
            timestamp = currentTime(),
            deviceId = hdUUID(4),
            clientId = hdUUID(5),
            type = Type.entries.random(),
            topic = hdUUID(6),
            cmd = hdUUID(3),
            payload = hdUUID(10),
            state = Random.nextBoolean(),
            onLine =  Random.nextBoolean(),
        )
    }
}

internal fun Hd_log.toEntity() = LogEntity(
    lid = this.lid,
    timestamp = this.timestamp,
    deviceId = this.device_id,
    clientId = this.client_id,
    type = LogEntity.typeOf(this.type),
    topic = this.topic,
    cmd = this.cmd,
    payload = this.payload,
    state = this.state,
    onLine = this.online
)