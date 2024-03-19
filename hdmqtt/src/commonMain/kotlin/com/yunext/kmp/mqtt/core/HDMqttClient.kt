package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState

 expect interface HDMqttClient {
    val clientId: String

    val state:HDMqttState

    fun init()

    fun connect(param: HDMqttParam, listener: HDMqttActionListener)

    fun subscribeTopic(
        topic: String,
        actionListener: HDMqttActionListener,
        listener: OnHDMqttMessageChangedListener,
    )

    fun unsubscribeTopic(topic: String, listener: HDMqttActionListener)

    fun publish(
        topic: String, payload: ByteArray,
        qos: Int,
        retained: Boolean, listener: HDMqttActionListener,
    )

    fun disconnect()

    fun clear()
}