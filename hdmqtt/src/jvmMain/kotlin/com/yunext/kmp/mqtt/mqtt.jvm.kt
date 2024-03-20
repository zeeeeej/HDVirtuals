package com.yunext.kmp.mqtt

import com.yunext.kmp.context.hdContext
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.core.JvmMQTTClientImpl
import com.yunext.kmp.mqtt.core.OnHDMqttMessageChangedListener
import com.yunext.kmp.mqtt.core.OnHDMqttStateChangedListener
import com.yunext.kmp.mqtt.core.OnMessageChangedListener
import com.yunext.kmp.mqtt.core.OnStateChangedListener
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState

actual typealias HDMqttClient = JvmMQTTClientImpl

actual fun createHdMqttClient(): HDMqttClient {
    return JvmMQTTClientImpl(hdContext)
}

actual val HDMqttClient.hdMqttState: HDMqttState
    get() = this.state

actual fun HDMqttClient.hdMqttInit() {
    this.init()
}

actual fun HDMqttClient.hdMqttConnect(
    param: HDMqttParam,
    listener: OnHDMqttActionListener,
    onHDMqttStateChangedListener: OnHDMqttStateChangedListener,
    onHDMqttMessageChangedListener: OnHDMqttMessageChangedListener,
) {
    this.onStateChangedListener = OnStateChangedListener { mqttState ->
        onHDMqttStateChangedListener.onChanged(this, mqttState)
    }
    this.onMessageChangedListener = OnMessageChangedListener { topic, message ->
        onHDMqttMessageChangedListener.onChanged(this, topic, message)
    }
    this.connect(param, listener)
}

actual fun HDMqttClient.hdMqttSubscribeTopic(
    topic: String,
    actionListener: OnHDMqttActionListener,
) {
    this.subscribeTopic(
        topic, actionListener
    )
}

actual fun HDMqttClient.hdMqttUnsubscribeTopic(
    topic: String,
    listener: OnHDMqttActionListener,
) {
    this.unsubscribeTopic(topic, listener)
}

actual fun HDMqttClient.hdMqttPublish(
    topic: String,
    payload: ByteArray,
    qos: Int,
    retained: Boolean,
    listener: OnHDMqttActionListener,
) {
    this.publish(topic, payload, qos, retained, listener)
}

actual fun HDMqttClient.hdMqttDisconnect() {
    this.disconnect()
}