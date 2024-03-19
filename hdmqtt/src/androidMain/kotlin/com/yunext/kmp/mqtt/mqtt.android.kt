package com.yunext.kmp.mqtt

import com.yunext.kmp.context.application
import com.yunext.kmp.context.hdContext
import com.yunext.kmp.mqtt.core.HDMqttClient
import com.yunext.kmp.mqtt.core.AndroidMQTTClientImpl
import com.yunext.kmp.mqtt.core.HDMqttActionListener
import com.yunext.kmp.mqtt.core.OnHDMqttMessageChangedListener
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState

 actual fun createHdMqttClient(): HDMqttClient {
    return AndroidMQTTClientImpl(hdContext.application)
}

//actual typealias HDMqttClient2 = AndroidMQTTClientImpl
//
//actual val HDMqttClient2.state: HDMqttState
//    get() = this.state

actual fun HDMqttClient.hdMqttInit() {
}

actual fun HDMqttClient.hdMqttConnect(
    param: HDMqttParam,
    listener: HDMqttActionListener,
) {
    this.connect(param,listener)
}

actual fun HDMqttClient.hdMqttSubscribeTopic(
    topic: String,
    actionListener: HDMqttActionListener,
    listener: OnHDMqttMessageChangedListener,
) {
    this.subscribeTopic(topic,actionListener,listener)
}

actual fun HDMqttClient.hdMqttUnsubscribeTopic(
    topic: String,
    listener: HDMqttActionListener,
) {
    this.unsubscribeTopic(topic,listener)
}

actual fun HDMqttClient.hdMqttPublish(
    topic: String,
    payload: ByteArray,
    qos: Int,
    retained: Boolean,
    listener: HDMqttActionListener,
) {
    this.publish(topic,payload,qos,retained,listener)
}

actual fun HDMqttClient.hdMqttDisconnect() {
    this.disconnect()
}

actual fun HDMqttClient.hdMqttClear() {
    this.clear()
}

//actual fun HDMqttClient(): HDMqttClient2 {
//    return HDMqttClient2(hdContext.application)
//}