package com.yunext.kmp.mqtt.virtuals.protocol.tsl

class Tsl(
    val id: String,
    val version: String,
    val productKey: String,
    val current: Boolean,
    val events: List<TslEvent>,
    val properties: List<TslProperty>,
    val services: List<TslService>
)


