package com.yunext.kmp.mqtt.data

sealed class MQTTState {
    data object Init : MQTTState()
    data object Connected : MQTTState()
    data object Disconnected : MQTTState()
}

val MQTTState.isConnected:Boolean
    get() = this == MQTTState.Connected