package com.yunext.kmp.mqtt.core

import android.content.Context
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.application
import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.data.display
import com.yunext.kmp.mqtt.data.isConnected
import com.yunext.kmp.mqtt.toMsg
import com.yunext.kmp.mqtt.utils.mqttError
import com.yunext.kmp.mqtt.utils.mqttInfo
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicReference

class AndroidMQTTClientImpl(hdContext: HDContext) : IHDMqttClient {

    private val ctx: Context = hdContext.application.applicationContext

    private lateinit var client: MqttAndroidClient

    private var stateInternal: AtomicReference<HDMqttState> = AtomicReference(HDMqttState.Init)

    override val tag: String
        get() = "laputa-${System.currentTimeMillis()}"

    override val state: HDMqttState
        get() = stateInternal.get()

    private val mTopics: CopyOnWriteArraySet<String> = CopyOnWriteArraySet()

    internal var onStateChangedListener: OnStateChangedListener? = null

    override fun init() {
        mqttInfo("mqtt-android-init")
//        val url = "ssl://${HDMQTTConstant.PATH}:${HDMQTTConstant.PORT}"
//        client = MqttAndroidClient(ctx.applicationContext, url, clientId).also {
//            it.setCallback(internalMqttCallback)
//        }
//
//        connect(
//            HDMqttParam(
//                "laputa",
//                "123456",
//                clientId = clientId,
//                url = url
//            ), object : HDMqttActionListener {
//                override fun onSuccess(token: Any?) {
//                    mqttInfo("mqtt-android-init connect success token:$token")
//                }
//
//                override fun onFailure(token: Any?, exception: Throwable?) {
//                    mqttInfo("mqtt-android-init connect fail token:$token exception:$exception")
//                }

//            })
    }

    internal var onMessageChangedListener: OnMessageChangedListener? = null

    private val internalMqttCallback = object : MqttCallbackExtended {
        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            mqttInfo("connectComplete reconnect:$reconnect @ $serverURI")
            onStateChanged(HDMqttState.Connected)
            if (reconnect) {
                // Because Clean Session is true, we need to re-subscribe
                try {
                    //mTopics.subscribeTopics()


                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }


        override fun messageArrived(topic: String, message: MqttMessage?) {
            try {
                mqttInfo(
                    "messageArrived $topic # $message ---> ${
                        message?.payload?.let {
                            String(it)
                        }
                    }"
                )
                handleMessage(topic, message?.toMsg())
            } catch (e: Throwable) {
                mqttError("messageArrived error：$e")
            }
        }

        override fun connectionLost(cause: Throwable?) {
            onStateChanged(HDMqttState.Disconnected)
            mqttError("connectionLost cause：$cause")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            mqttError("deliveryComplete token：${token?.client?.clientId}")
        }
    }

    override fun connect(param: HDMqttParam, listener: OnActionListener) {
        mqttInfo("mqtt-android-connect")
        mqttInfo(param.display)

        client = MqttAndroidClient(ctx.applicationContext, param.url, param.clientId).also {
            it.setCallback(internalMqttCallback)
        }
        val options = MqttConnectOptions().apply {
            // 是否自动重新连接。当客户端网络异常或进入后台后导致连接中断，在这期间会不断的尝试重连，
            // 重连等待最初会等待1 秒钟, 每次重连失败等待时间就会加倍，直到 2 分钟，此时延迟将保持在 2 分钟。
            isAutomaticReconnect = true
            // 是否自动清除 session. 注意如果为 true 则会清除session. 会导致如果你掉线的期间，
            // 你所订阅的topic有新的消息，等你重新连接上后因为session被清除了，你将无法接收到在你
            // 离线期间的新消息
            isCleanSession = true
            connectionTimeout = 60
            keepAliveInterval = 60

            // setWill(HadCategory.STATUS.createTopic(device.appType().strValue, device.mac), byteArrayOf(),1,true)
            this.password = param.password.toCharArray()
            this.userName = param.username
            if (param.ssl) {
                socketFactory = DefaultSSL.defaultFactory()
            }
        }

        client.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                mqttInfo("connect onSuccess $asyncActionToken")
                listener.onSuccess(asyncActionToken ?: "")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                mqttError("connect onFailure $asyncActionToken # $exception")
                listener.onFailure(asyncActionToken ?: "", exception)

            }

        })
    }

    override fun subscribeTopic(
        topic: String,
        actionListener: OnActionListener,
//        listener: OnMessageChangedListener,
    ) {
        mqttInfo("mqtt-android-subscribeTopic")
        if (!::client.isInitialized) return
//        if (checkIsConnected()) {
        client.subscribe(topic, 0, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                mqttInfo("subscribeTopic onSuccess $topic $asyncActionToken")
                actionListener.onSuccess(asyncActionToken)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                mqttError("subscribeTopic onFailure $topic $asyncActionToken $exception")
                actionListener.onFailure(asyncActionToken, exception)
            }
        })
//        } else {
//            mqttError("subscribeTopic error 连接已断开")
//            listener.onFailure(null, IllegalStateException("连接已断开"))
//        }

    }

    override fun unsubscribeTopic(topic: String, listener: OnActionListener) {
        mqttInfo("mqtt-android-unsubscribeTopic")
        if (!::client.isInitialized) return
        client.unsubscribe(topic, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                mqttInfo("subscribeTopic onSuccess $topic $asyncActionToken")
                listener.onSuccess(asyncActionToken)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                mqttError("subscribeTopic onFailure $topic $asyncActionToken $exception")
                listener.onFailure(asyncActionToken, exception)
            }
        })
    }


    override fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: OnActionListener,
    ) {
        mqttInfo("mqtt-android-publish")
        if (!::client.isInitialized) return
        client.publish(
            topic,
            payload,
            qos,
            retained,
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    mqttInfo("subscribeTopic onSuccess $topic $asyncActionToken")
                    listener.onSuccess(asyncActionToken)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    mqttError("subscribeTopic onFailure $topic $asyncActionToken $exception")
                    listener.onFailure(asyncActionToken, exception)
                }
            })
    }

    override fun disconnect() {
        mqttInfo("mqtt-android-disconnect")
        if (!::client.isInitialized) return
        client.disconnect()
        onStateChanged(HDMqttState.Disconnected)
        clear()
    }

    private fun clear() {
        mqttInfo("mqtt-android-clear")
        onMessageChangedListener = null
        onStateChangedListener = null
    }


    private fun handleMessage(topic: String, message: HDMqttMessage?) {
        if (topic.isBlank() || message == null) return
        onMessageChangedListener?.onChanged(topic, message)
    }

    private fun checkIsConnected(): Boolean =
        (::client.isInitialized && client.isConnected && stateInternal.get().isConnected)

    private fun onStateChanged(state: HDMqttState) {
        mqttInfo("onStateChanged state:$state")
        stateInternal.set(state)
        onStateChangedListener?.onChanged(state)
    }
}