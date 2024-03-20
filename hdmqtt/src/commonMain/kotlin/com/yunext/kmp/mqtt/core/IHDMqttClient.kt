package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState

internal interface IHDMqttClient {
    val tag: String

    val state: HDMqttState

    fun init()

    fun connect(
        param: HDMqttParam,
        listener: OnActionListener,
    )

    fun subscribeTopic(
        topic: String,
        actionListener: OnActionListener,
//        listener: OnMessageChangedListener,
    )

    fun unsubscribeTopic(
        topic: String,
        listener: OnActionListener,
    )

    fun publish(
        topic: String, payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: OnActionListener,
    )

    fun disconnect()

//    fun clear()
}