package com.yunext.kmp.mqtt.core

import MQTTClient
import com.yunext.kmp.common.util.hdUUID
import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.utils.mqttError
import com.yunext.kmp.mqtt.utils.mqttInfo
import com.yunext.kmp.mqtt.utils.mqttWarn
import kotlinx.coroutines.delay
import mqtt.MQTTVersion
import mqtt.Subscription
import mqtt.packets.Qos
import mqtt.packets.mqttv5.ReasonCode
import mqtt.packets.mqttv5.SubscriptionOptions
import socket.tls.TLSClientSettings

@Deprecated("")
internal class KMQTTClient : IHDMqttClient {

    override val tag: String
        get() = "tag-kmqtt-${hdUUID(4)}"

    private var mqttClient: MQTTClient? = null


    var onStateChangedListener: OnStateChangedListener? = null
    var onMessageChangedListener: OnMessageChangedListener? = null

    override fun init() {
        mqttInfo("::init")
    }

    private var retry = false


    @OptIn(ExperimentalUnsignedTypes::class)
    override fun connect(
        param: HDMqttParam,
        listener: OnActionListener,
    ) {
        try {
            val url = param.shortUrl
            val port = param.port
            val clientId = param.clientId
            val username = param.username
            val password = param.password
            mqttInfo("::connect param:$param ")
            mqttInfo("::connect url         :$url ")
            mqttInfo("::connect port        :$port ")
            mqttInfo("::connect clientId    :$clientId ")
            mqttInfo("::connect username    :$username ")
            mqttInfo("::connect password    :$password ")
            if (mqttClient != null) {
                mqttWarn("why your client is not null? mqttClient info : $mqttClient")
            }

            onStateChangedListener?.onChanged(HDMqttState.Init)
            mqttClient = MQTTClient(
                mqttVersion = MQTTVersion.MQTT3_1_1,
                address = url,
                clientId = clientId,
                port = port.toInt(),
                userName = username,
                password = password.encodeToByteArray().toUByteArray(),
                //            tls = TLSClientSettings(serverCertificate = "emqxsl.ca.ios.crt"),
//                tls = param.tls?.let {
//                    TLSClientSettings(serverCertificate = it)
//                },
                tls = null,//TLSClientSettings(version = "ssl"),
                onConnected = {
                    mqttInfo("::connect onConnected $it")
                    listener.onSuccess(mqttClient)
                    stateInternal = HDMqttState.Connected
                    onStateChangedListener?.onChanged(HDMqttState.Connected)

                },

                onDisconnected = {
                    mqttInfo("::connect onDisconnected $it")
                    listener.onFailure(mqttClient, null)
                    stateInternal = HDMqttState.Disconnected
                    onStateChangedListener?.onChanged(HDMqttState.Disconnected)
                },
                //            tls = TLSClientSettings(
                //                serverCertificate = "mosquitto.org.crt",
                //            ),

                publishReceived = { mqttPublish ->
                    mqttInfo(
                        """
                        -*-*-*-*-*-*-    
                        ::connect publishReceived
                        topicName   :   ${mqttPublish.topicName}
                        dup         :   ${mqttPublish.dup}
                        qos         :   ${mqttPublish.qos}
                        payload     :   ${mqttPublish.payload}
                        retain      :   ${mqttPublish.retain}
                        timestamp   :   ${mqttPublish.timestamp}
                        packetId    :   ${mqttPublish.packetId}
                        -*-*-*-*-*-*-*-*-*-*-*-*-      
                    """.trimIndent()
                    )
                    val msg = HDMqttMessage(
                        payload = mqttPublish.payload?.toByteArray() ?: byteArrayOf(),
                        retained = mqttPublish.retain,
                        qos = mqttPublish.qos.value,
                        messageId = mqttPublish.packetId?.toInt() ?: -1,
                        dup = mqttPublish.dup,

                        )
                    onMessageChangedListener?.onChanged(
                        mqttPublish.topicName, msg
                    )
                }
            )


//            mqttClient?.step()

            // mqttClient?.run()
        } catch (e: Exception) {
            mqttError("::connect error $e")
            e.printStackTrace()
            stateInternal = HDMqttState.Disconnected
            onStateChangedListener?.onChanged(HDMqttState.Disconnected)
        }
    }

    override fun subscribeTopic(
        topic: String,
        actionListener: OnActionListener,
    ) {
        mqttClient?.run()
//        mqttInfo("::subscribeTopic topic:$topic")
//        val subscription = Subscription(topic, SubscriptionOptions(Qos.EXACTLY_ONCE))
//        val subscriptions = listOf(subscription)
//        try {
//            mqttClient?.subscribe(subscriptions)
//            actionListener.onSuccess(this)
//        } catch (e: Exception) {
//            actionListener.onFailure(this, e)
//        }
    }

    override fun unsubscribeTopic(topic: String, listener: OnActionListener) {
        mqttInfo("::unsubscribeTopic topic:$topic")
        try {
            mqttClient?.unsubscribe(listOf(topic))
            listener.onSuccess(this)
        } catch (e: Exception) {
            listener.onFailure(this, e)
        } finally {
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: OnActionListener,
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
        try {
            mqttInfo("::disconnect ")
            mqttClient?.disconnect(ReasonCode.ADMINISTRATIVE_ACTION)
        } catch (e: Exception) {
            mqttError("::disconnect error $e")
        } finally {
            mqttClient = null
            onStateChangedListener?.onChanged(HDMqttState.Disconnected)
            clear()
        }
    }

    private fun clear() {
        mqttInfo("::clear ")
        onStateChangedListener = null
        onMessageChangedListener = null
    }

    private var stateInternal: HDMqttState = HDMqttState.Init
    override val state: HDMqttState
        get() = stateInternal
}