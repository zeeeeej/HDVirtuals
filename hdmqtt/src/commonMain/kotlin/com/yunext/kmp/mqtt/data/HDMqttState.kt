package com.yunext.kmp.mqtt.data

sealed class HDMqttState {
    data object Init : HDMqttState()
    data object Connected : HDMqttState()
    data object Disconnected : HDMqttState()
}

val HDMqttState.isConnected:Boolean
    get() = this == HDMqttState.Connected