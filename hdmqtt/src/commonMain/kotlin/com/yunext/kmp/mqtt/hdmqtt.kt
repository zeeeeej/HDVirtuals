package com.yunext.kmp.mqtt

import com.yunext.kmp.context.HDContext
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.core.OnHDMqttMessageChangedListener
import com.yunext.kmp.mqtt.core.OnHDMqttStateChangedListener
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState

expect class HDMqttClient internal constructor(hdContext: HDContext)

expect fun createHdMqttClient(): HDMqttClient

fun hdDebug(debug: Boolean) {
    HDMQTTConstant.debug = debug
}

expect val HDMqttClient.hdMqttState: HDMqttState

expect fun HDMqttClient.hdMqttInit()
expect fun HDMqttClient.hdMqttConnect(
    param: HDMqttParam,
    listener: OnHDMqttActionListener,
    onHDMqttStateChangedListener: OnHDMqttStateChangedListener,
    onHDMqttMessageChangedListener: OnHDMqttMessageChangedListener,
)

expect fun HDMqttClient.hdMqttSubscribeTopic(
    topic: String,
    actionListener: OnHDMqttActionListener
)

expect fun HDMqttClient.hdMqttUnsubscribeTopic(
    topic: String,
    listener: OnHDMqttActionListener,
)

expect fun HDMqttClient.hdMqttPublish(
    topic: String,
    payload: ByteArray,
    qos: Int,
    retained: Boolean,
    listener: OnHDMqttActionListener,
)

expect fun HDMqttClient.hdMqttDisconnect()
