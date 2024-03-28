package com.yunext.kmp.mqtt

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.hdContext
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.core.IOSHDMqttClientImpl
import com.yunext.kmp.mqtt.core.OnActionListener
import com.yunext.kmp.mqtt.core.OnHDMqttMessageChangedListener
import com.yunext.kmp.mqtt.core.OnHDMqttStateChangedListener
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import kotlinx.coroutines.delay

actual typealias HDMqttClient = IOSHDMqttClientImpl

actual fun createHdMqttClient(): HDMqttClient {
    return IOSHDMqttClientImpl(hdContext)
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
    this.registerOnStateChangedListener { mqttState ->
        onHDMqttStateChangedListener.onChanged(this, mqttState)
    }
    this.registerOnMessageChangedListener { topic, message ->
        onHDMqttMessageChangedListener.onChanged(this, topic, message)
    }
    val onActionListener = object : OnActionListener {
        override fun onSuccess(token: Any?) {
            listener.onSuccess(token)

        }

        override fun onFailure(token: Any?, exception: Throwable?) {
            listener.onFailure(token, exception)
        }

    }
    this.connect(param, onActionListener)
}

actual fun HDMqttClient.hdMqttSubscribeTopic(
    topic: String,
    actionListener: OnHDMqttActionListener,
) {
    this.subscribeTopic(topic, actionListener)
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