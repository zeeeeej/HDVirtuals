package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.utils.mqttInfo
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class IOSHDMqttClientImpl : HDMqttClient {

    private val kmqttClient = KMQTTClient()
    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("KMQTTClient"))
    override val clientId: String
        get() = "mqtt-ios-${kmqttClient.hashCode()}"

    override fun init() {
        coroutineScope.launch {
            kmqttClient.init()
        }

    }

    override fun connect(param: HDMqttParam, listener: HDMqttActionListener) {
        coroutineScope.launch {
            kmqttClient.connect(param, listener)
        }
    }

    override fun subscribeTopic(
        topic: String, actionListener: HDMqttActionListener,
        listener: OnHDMqttMessageChangedListener,
    ) {
        mqttInfo("mqtt-ios-subscribeTopic")
        coroutineScope.launch {
            kmqttClient.subscribeTopic(topic, actionListener, listener)
        }
    }

    override fun unsubscribeTopic(topic: String, listener: HDMqttActionListener) {
        mqttInfo("mqtt-ios-unsubscribeTopic")
        coroutineScope.launch {
            kmqttClient.unsubscribeTopic(topic, listener)
        }
    }

    override fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: HDMqttActionListener,
    ) {
        mqttInfo("mqtt-ios-publish")
        coroutineScope.launch {
            kmqttClient.publish(topic, payload, qos, retained, listener)
        }
    }

    override fun disconnect() {
        mqttInfo("mqtt-ios-disconnect")
        coroutineScope.launch {
            kmqttClient.disconnect()
        }
    }

    override fun clear() {
        mqttInfo("mqtt-ios-clear")
        coroutineScope.launch {
            kmqttClient.clear()
        }
    }

    override val state: HDMqttState
        get() = kmqttClient.state
}