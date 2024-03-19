package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.utils.mqttError
import com.yunext.kmp.mqtt.utils.mqttInfo
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

 class JvmMQTTClientImpl : HDMqttClient {
    override val clientId: String
        get() = "mqtt-jvm-${System.currentTimeMillis()}"

    private var currentClint: MqttClient? = null

    var onStateChangedListener: OnHDMqttStateChangedListener? = null
    var onMessageChangedListener: OnHDMqttMessageChangedListener? = null

    override fun init() {
        mqttInfo("mqtt-desktop-init")
//        val broker = "ssl://${HDMQTTConstant.PATH}:${HDMQTTConstant.PORT}"
//        val topic  = "xpl/abc"
//        val qos = 2
//        val memoryPersistence = MemoryPersistence()
//        val content = "hello  i am from desktop"
//        val ssl = true
//        try {
//            val client =  MqttClient(broker,clientId,memoryPersistence)
//            val connOpts = MqttConnectOptions().apply {
//                userName = "laputa"
//                this.password = "123456".toCharArray()
//                defaultFactory(ssl)
//            }
//            connOpts.isCleanSession = true
//            mqttInfo("Connecting to broker: $broker")
//            client.connect(connOpts)
//            client.subscribeWithResponse(topic
//            ) { tp, message ->
//
//                mqttInfo("$tp # $message")
//            }
//            mqttInfo("Connected")
//            mqttInfo("Publishing message: $content")
//            val message = MqttMessage(content.toByteArray())
//            message.qos = qos
//            client.publish(topic, message)
//
//            mqttInfo("Message published")
////            client.disconnect()
//            mqttInfo("Disconnected")
//        } catch (me: MqttException) {
//            mqttError("reason "+me.reasonCode);
//            mqttError("msg "+me.message)
//            mqttError("loc "+me.localizedMessage)
//            mqttError("cause "+me.cause)
//            mqttError("excep $me")
//            me.printStackTrace();
//        } finally {
//        }


    }

    override fun connect(param: HDMqttParam, listener: HDMqttActionListener) {
        mqttInfo("mqtt-desktop-connect")
        val broker = param.url
//        val topic  = "xpl/abc"
//        val topic  = "/skeleton/QR-12TRWQ4/fe495a3be9c7/down"
//        val topic = "/skeleton/QR-12TRWQ4/fe495a3be9c7/up"
//        val qos = 2
        val memoryPersistence = MemoryPersistence()
//        val content = "hello  i am from desktop"
        val ssl = param.ssl
        try {
            val client = MqttClient(broker, param.clientId, memoryPersistence)
            val connOpts = MqttConnectOptions().apply {
                this.userName = param.username
                this.password = param.password.toCharArray()
                this.defaultFactory(ssl)
            }
            client.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    stateInternal = HDMqttState.Disconnected
                    onStateChangedListener?.onChanged(this@JvmMQTTClientImpl, state)
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    // todo
                    onMessageChangedListener?.onChanged(
                        this@JvmMQTTClientImpl, topic, HDMqttMessage(
                            payload = message.payload,
                            retained = message.isRetained,
                            qos = message.qos,
                            messageId = message.id,
                            dup = message.isDuplicate,
                        )
                    )
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }

            })
            connOpts.isCleanSession = true
            mqttInfo("Connecting to broker: $broker")
            val result = client.connectWithResult(connOpts)
            result.waitForCompletion(5000L)
            currentClint = client
            mqttInfo("Connecting Success: $result")
            listener.onSuccess(result)
            stateInternal = HDMqttState.Connected
            onStateChangedListener?.onChanged(this@JvmMQTTClientImpl, state)
        } catch (me: MqttException) {
            mqttError("reason   :" + me.reasonCode);
            mqttError("msg      :" + me.message)
            mqttError("loc      :" + me.localizedMessage)
            mqttError("cause    :" + me.cause)
            mqttError("exception: $me")
            me.printStackTrace();
            listener.onFailure(null, me)
            stateInternal = HDMqttState.Disconnected
            onStateChangedListener?.onChanged(this@JvmMQTTClientImpl, state)
        } finally {
        }
    }

    override fun subscribeTopic(
        topic: String, actionListener: HDMqttActionListener,
        listener: OnHDMqttMessageChangedListener,
    ) {
        mqttInfo("mqtt-desktop-subscribeTopic")
        val client = currentClint ?: return
        client.subscribeWithResponse(
            topic
        ) { tp, message ->
            mqttInfo("$tp # $message")
            listener.onChanged(
                this, topic, HDMqttMessage(
                    payload = message.payload,
                    retained = message.isRetained,
                    qos = message.qos,
                    messageId = message.id,
                    dup = message.isDuplicate,
                )
            )

        }
    }

    override fun unsubscribeTopic(topic: String, listener: HDMqttActionListener) {
        mqttInfo("mqtt-desktop-unsubscribeTopic")
        val client = currentClint ?: return
        try {
            client.unsubscribe(topic)
            listener.onSuccess(client)
        } catch (e: Exception) {
            listener.onFailure(client, e)
        }
    }

    override fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: HDMqttActionListener,
    ) {
        mqttInfo("mqtt-desktop-publish")
        val client = currentClint ?: return
        try {
            client.publish(topic, payload, qos, retained)
            listener.onSuccess(client)
        } catch (e: Exception) {
            listener.onFailure(client, e)
        }
    }

    override fun disconnect() {
        mqttInfo("mqtt-desktop-disconnect")
        val client = currentClint ?: return
        client.disconnect()
    }

    override fun clear() {
        mqttInfo("mqtt-desktop-clear")
        val client = currentClint ?: return
        currentClint = null
    }

    private var stateInternal: HDMqttState = HDMqttState.Init
    override val state: HDMqttState
        get() = stateInternal
}