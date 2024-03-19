package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState

 actual interface HDMqttClient {
    @Deprecated("delete")
    actual val clientId: String
    actual fun init()
    actual fun connect(
        param: HDMqttParam,
        listener: HDMqttActionListener,
    )

    actual fun subscribeTopic(
        topic: String,
        actionListener: HDMqttActionListener,
        listener: OnHDMqttMessageChangedListener
    )

    actual fun unsubscribeTopic(
        topic: String,
        listener: HDMqttActionListener,
    )

    actual fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: HDMqttActionListener,
    )

    actual fun disconnect()
    actual fun clear()
    actual val state: HDMqttState

}

