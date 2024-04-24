package com.yunext.kmp.mqtt.virtuals.protocol.tsl

import kotlinx.serialization.Serializable

@Serializable
enum class TslServiceCallType(val text: String) {
    ASYNC("async"),
    SYNC("sync");

    companion object {
        fun from(text: String): TslServiceCallType {
            return when (text) {
                ASYNC.text -> ASYNC
                SYNC.text -> SYNC
                else -> throw TslException("不支持的TslServiceCallType:$text")
            }
        }
    }
}