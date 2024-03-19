package com.yunext.kmp.mqtt

import com.yunext.kmp.mqtt.core.HDMqttActionListener
import com.yunext.kmp.mqtt.core.HDMqttClient
import com.yunext.kmp.mqtt.core.OnHDMqttMessageChangedListener
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState

 expect fun createHdMqttClient(): HDMqttClient


//expect val HDMqttClient.state: HDMqttState

expect fun HDMqttClient.hdMqttInit()
expect fun HDMqttClient.hdMqttConnect(
    param: HDMqttParam,
    listener: HDMqttActionListener,
)

expect fun HDMqttClient.hdMqttSubscribeTopic(
    topic: String,
    actionListener: HDMqttActionListener,
    listener: OnHDMqttMessageChangedListener,
)

expect fun HDMqttClient.hdMqttUnsubscribeTopic(
    topic: String,
    listener: HDMqttActionListener,
)

expect fun HDMqttClient.hdMqttPublish(
    topic: String,
    payload: ByteArray,
    qos: Int,
    retained: Boolean,
    listener: HDMqttActionListener,
)

expect fun HDMqttClient.hdMqttDisconnect()
expect fun HDMqttClient.hdMqttClear()
