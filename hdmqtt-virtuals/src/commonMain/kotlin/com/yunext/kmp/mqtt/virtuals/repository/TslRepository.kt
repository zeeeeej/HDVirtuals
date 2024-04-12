package com.yunext.kmp.mqtt.virtuals.repository

import com.yunext.kmp.context.HDContext
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.Tsl


interface TslRepository {
    val hdContext:HDContext
    suspend fun getTsl(clientId:String,deviceId:String): Tsl?
    suspend fun updateTsl(clientId:String,deviceId:String): Tsl?
}