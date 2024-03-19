package com.yunext.kmp.mqtt.core

import MQTTClient
import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.utils.mqttInfo
import com.yunext.kmp.mqtt.utils.mqttWarn
import mqtt.MQTTVersion
import mqtt.Subscription
import mqtt.packets.Qos
import mqtt.packets.mqttv5.ReasonCode
import mqtt.packets.mqttv5.SubscriptionOptions
import socket.tls.TLSClientSettings

internal class KMQTTClient : HDMqttClient {


    override val clientId: String
        get() = ""

    private var mqttClient: MQTTClient? = null


    var onStateChangedListener: OnHDMqttStateChangedListener? = null
    var onMessageChangedListener: OnHDMqttMessageChangedListener? = null
    private val onChangedListenerMap: MutableMap<String, OnHDMqttMessageChangedListener> =
        mutableMapOf()

    fun registerOnMessageChangedListener(topic: String, listener: OnHDMqttMessageChangedListener) {
        onChangedListenerMap[topic] = listener
    }

    fun unRegisterOnMessageChangedListener(topic: String) {
        onChangedListenerMap.remove(topic)
    }


    override fun init() {
        mqttInfo("::init")
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun connect(
        param: HDMqttParam,
        listener: HDMqttActionListener,
    ) {
        mqttInfo("::connect param:$param")
        if (mqttClient != null) {
            mqttWarn("why your client is not null? mqttClient info : $mqttClient")
        }
        onStateChangedListener?.onChanged(this, HDMqttState.Init)
        mqttClient = MQTTClient(
            mqttVersion = MQTTVersion.MQTT3_1_1,
            address = param.shortUrl,
            clientId = param.clientId,
            port = param.port.toInt(),
            userName = param.username,
            password = param.password.encodeToByteArray().toUByteArray(),
//            tls = TLSClientSettings(serverCertificate = "emqxsl.ca.ios.crt"),
            tls = param.tls?.let {
                TLSClientSettings(serverCertificate = it)
            },
            onConnected = {
                mqttInfo("::connect onConnected $it")
                listener.onSuccess(mqttClient)
                stateInternal = HDMqttState.Connected
                onStateChangedListener?.onChanged(this, HDMqttState.Connected)

            },
            onDisconnected = {
                mqttInfo("::connect onDisconnected $it")
                listener.onFailure(mqttClient, null)
                stateInternal = HDMqttState.Disconnected
                onStateChangedListener?.onChanged(this, HDMqttState.Disconnected)
            },
//            tls = TLSClientSettings(
//                serverCertificate = "mosquitto.org.crt",
//            ),

            publishReceived = { mqttPublish ->
                mqttInfo(
                    """
                    ::connect publishReceived
                    topicName   :   ${mqttPublish.topicName}
                    dup         :   ${mqttPublish.dup}
                    qos         :   ${mqttPublish.qos}
                    payload     :   ${mqttPublish.payload}
                    retain      :   ${mqttPublish.retain}
                    timestamp   :   ${mqttPublish.timestamp}
                    packetId    :   ${mqttPublish.packetId}
                """.trimIndent()
                )
                onMessageChangedListener?.onChanged(
                    this, mqttPublish.topicName, HDMqttMessage(
                        payload = mqttPublish.payload?.toByteArray() ?: byteArrayOf(),
                        retained = mqttPublish.retain,
                        qos = mqttPublish.qos.value,
                        messageId = mqttPublish.packetId?.toInt() ?: -1,
                        dup = mqttPublish.dup,

                        )
                )
            }
        )


//            mqttClient?.step()
        mqttClient?.run()
//        val topic = "/skeleton/QR-12TRWQ4/fe495a3be9c7/up"
//        mqttClient = MQTTClient(
//            mqttVersion = MQTTVersion.MQTT3_1_1,
//            address = "o5dae913.cn-hangzhou.emqx.cloud",
//            clientId = "kmp-ios-${hdUUID(4)}",
//            port = 15925,
//            userName = param.username,
//            password = param.password.encodeToByteArray().toUByteArray(),
////            tls = TLSClientSettings(serverCertificate = "emqxsl.ca.ios.crt"),
//            tls = TLSClientSettings(serverCertificate = KEY_TLS),
//            onConnected = {
//                mqttInfo("::connect onConnected $it")
//
//                subscribeTopic(topic, object : HDMqttActionListener {
//                    override fun onSuccess(token: Any?) {
//
//                    }
//
//                    override fun onFailure(token: Any?, exception: Throwable?) {
//                    }
//
//                }
//                ) { mqttClient, topic, message -> }
//            },
//            onDisconnected = {
//                mqttInfo("::connect onDisconnected $it")
//            },
////            tls = TLSClientSettings(
////                serverCertificate = "mosquitto.org.crt",
////            ),
//
//            publishReceived = { mqttPublish ->
//                mqttInfo(
//                    """
//                    ::connect publishReceived
//                    topicName   :   ${mqttPublish.topicName}
//                    dup         :   ${mqttPublish.dup}
//                    qos         :   ${mqttPublish.qos}
//                    payload     :   ${mqttPublish.payload}
//                    retain      :   ${mqttPublish.retain}
//                    timestamp   :   ${mqttPublish.timestamp}
//                    packetId    :   ${mqttPublish.packetId}
//                """.trimIndent()
//                )
//            }
//        )
//
//
//        coroutineScope.launch() {
////            mqttClient?.step()
//            mqttClient?.run()
//        }

    }

    override fun subscribeTopic(
        topic: String,
        actionListener: HDMqttActionListener,
        listener: OnHDMqttMessageChangedListener,
    ) {
        mqttInfo("::subscribeTopic topic:$topic")
        val subscription = Subscription(topic, SubscriptionOptions(Qos.EXACTLY_ONCE))
        val subscriptions = listOf(subscription)
        try {
            mqttClient?.subscribe(subscriptions)
            registerOnMessageChangedListener(topic, listener)
            actionListener.onSuccess(this)
        } catch (e: Exception) {
            unRegisterOnMessageChangedListener(topic)
            actionListener.onFailure(this, e)
        }
    }

    override fun unsubscribeTopic(topic: String, listener: HDMqttActionListener) {
        mqttInfo("::unsubscribeTopic topic:$topic")
        try {
            mqttClient?.unsubscribe(listOf(topic))
            listener.onSuccess(this)
        } catch (e: Exception) {
            listener.onFailure(this, e)
        } finally {
            unRegisterOnMessageChangedListener(topic)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: HDMqttActionListener,
    ) {
        mqttInfo(
            """
            ::publish
            topic       :   $topic
            payload     :   $payload
            qos         :   $qos
            retained    :   $retained
        """.trimIndent()
        )

        try {
            mqttClient?.publish(
                retain = retained,
                qos = Qos.EXACTLY_ONCE,
                topic = topic,
                payload = payload.toUByteArray()
            )
            listener.onSuccess(this)
        } catch (e: Exception) {
            listener.onFailure(this, e)
        }
    }

    override fun disconnect() {
        mqttInfo("::disconnect ")
        mqttClient?.disconnect(ReasonCode.USE_ANOTHER_SERVER)
        mqttClient = null
    }

    override fun clear() {
        mqttInfo("::clear ")
        onChangedListenerMap.clear()
        onStateChangedListener = null
        onMessageChangedListener = null
    }

    private var stateInternal: HDMqttState = HDMqttState.Init
    override val state: HDMqttState
        get() = stateInternal
}