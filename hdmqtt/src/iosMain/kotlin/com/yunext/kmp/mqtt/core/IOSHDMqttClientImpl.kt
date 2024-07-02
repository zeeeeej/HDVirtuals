package com.yunext.kmp.mqtt.core

import com.yunext.kmp.context.HDContext
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.utils.mqttInfo
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Deprecated("")
class IOSHDMqttClientImpl(hdContext: HDContext) : IHDMqttClient {
    private val kmqttClient = KMQTTClient()
    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("KMQTTClient"))
    override val tag: String
        get() = "tag-ios-${kmqttClient.tag}"

    override fun init() {
        coroutineScope.launch {
            kmqttClient.init()
        }

    }

    internal fun registerOnStateChangedListener(listener: OnStateChangedListener) {
        coroutineScope.launch {
            kmqttClient.onStateChangedListener = listener
        }
    }

    internal fun registerOnMessageChangedListener(listener: OnMessageChangedListener) {
        coroutineScope.launch {
            kmqttClient.onMessageChangedListener = listener
        }
    }

    override fun connect(param: HDMqttParam, listener: OnActionListener) {
        coroutineScope.launch {
            kmqttClient.connect(param, listener)
        }
    }

    override fun subscribeTopic(
        topic: String, actionListener: OnActionListener,
    ) {
        mqttInfo("mqtt-ios-subscribeTopic")
        coroutineScope.launch {
            kmqttClient.subscribeTopic(topic, actionListener)
        }
    }

    override fun unsubscribeTopic(topic: String, listener: OnActionListener) {
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
        listener: OnActionListener,
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

    override val state: HDMqttState
        get() = kmqttClient.state
}