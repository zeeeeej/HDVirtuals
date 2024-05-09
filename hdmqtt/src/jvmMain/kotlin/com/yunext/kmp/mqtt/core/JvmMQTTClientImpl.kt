package com.yunext.kmp.mqtt.core

import com.yunext.kmp.context.HDContext
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

class JvmMQTTClientImpl(hdContext: HDContext) : IHDMqttClient {
    override val tag: String
        get() = "tag-jvm-${System.currentTimeMillis()}"

    private var currentClint: MqttClient? = null

    private var _param: HDMqttParam? = null

    val param: HDMqttParam?
        get() = _param
    internal var onStateChangedListener: OnStateChangedListener? = null
    internal var onMessageChangedListener: OnMessageChangedListener? = null

    override fun init() {
        mqttInfo("mqtt-desktop-init")
    }

    override fun connect(param: HDMqttParam, listener: OnActionListener) {
        mqttInfo("mqtt-desktop-connect")
        try {
            this._param = param
            val broker = param.url
            val memoryPersistence = MemoryPersistence()
            val ssl = param.ssl
            val client = MqttClient(broker, param.clientId, memoryPersistence)
            val connOpts = MqttConnectOptions().apply {
                this.userName = param.username
                this.password = param.password.toCharArray()
                this.defaultFactory(ssl)
            }
            client.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    stateInternal = HDMqttState.Disconnected
                    onStateChangedListener?.onChanged(state)
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    // todo
                    onMessageChangedListener?.onChanged(
                        topic, HDMqttMessage(
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
            onStateChangedListener?.onChanged(state)
        } catch (me: MqttException) {
            mqttError("reason   :" + me.reasonCode);
            mqttError("msg      :" + me.message)
            mqttError("loc      :" + me.localizedMessage)
            mqttError("cause    :" + me.cause)
            mqttError("exception: $me")
            me.printStackTrace();
            listener.onFailure(null, me)
            stateInternal = HDMqttState.Disconnected
            onStateChangedListener?.onChanged(state)
        } finally {
        }
    }

    override fun subscribeTopic(
        topic: String, actionListener: OnActionListener,
    ) {
        mqttInfo("mqtt-desktop-subscribeTopic")
        val client = currentClint ?: return
        client.subscribeWithResponse(
            topic
        ) { tp, message ->
            mqttInfo("$tp # $message")
            onMessageChangedListener?.onChanged(
                topic, HDMqttMessage(
                    payload = message.payload,
                    retained = message.isRetained,
                    qos = message.qos,
                    messageId = message.id,
                    dup = message.isDuplicate,
                )
            )
        }
    }

    override fun unsubscribeTopic(topic: String, listener: OnActionListener) {
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
        listener: OnActionListener,
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
        onStateChangedListener?.onChanged(HDMqttState.Disconnected)
        clear()
    }

    private fun clear() {
        mqttInfo("mqtt-desktop-clear")
        currentClint ?: return
        currentClint = null
        this._param = null
    }

    private var stateInternal: HDMqttState = HDMqttState.Init
    override val state: HDMqttState
        get() = stateInternal
}