package com.yunext.kmp.mqtt.core

import com.yunext.kmp.context.HDContext
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.hdClientId
import com.yunext.kmp.mqtt.interop.HDCocoaMQTTInterOpIn
import com.yunext.kmp.mqtt.interop.HDCocoaMQTTInterOpOut
import com.yunext.kmp.mqtt.interop.HDInterOpMqttMessage
import com.yunext.kmp.mqtt.interop.HDInterOpState
import com.yunext.kmp.mqtt.interop.OnHDInterOpMqttMessageChanged
import com.yunext.kmp.mqtt.interop.OnHDInterOpStateChanged
import com.yunext.kmp.mqtt.utils.mqttError
import com.yunext.kmp.mqtt.utils.mqttInfo
import com.yunext.kmp.mqtt.utils.mqttWarn
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class InterOpHDMqttClientImpl(hdContext: HDContext) : IHDMqttClient {

    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("KMQTTClient"))

    // 标志
    private var reference: String? = null
    private var _param: HDMqttParam? = null

    val param: HDMqttParam?
        get() = _param

    // kotlin -> Swift
    private val outOpt = HDCocoaMQTTInterOpOut

    // kotlin <- Swift
    private val inOpt = HDCocoaMQTTInterOpIn

    override val tag: String
        get() = "tag-$TAG"

    private var internalState: HDMqttState = HDMqttState.Init

    private var onStateChangedListener: OnStateChangedListener? = null
    private var onMessageChangedListener: OnMessageChangedListener? = null
    internal fun registerOnStateChangedListener(listener: OnStateChangedListener) {
        onStateChangedListener = listener
    }

    internal fun registerOnMessageChangedListener(listener: OnMessageChangedListener) {
        onMessageChangedListener = listener
    }

    private val messageCallback: OnHDInterOpMqttMessageChanged = { message: HDInterOpMqttMessage ->
        mqttInfo("$TAG::init messageChannel $message")
        if (message.reference == this@InterOpHDMqttClientImpl.reference) {
            onMessageChangedListener?.onChanged(
                message.topic, message.message
            )
        }
    }

    private val stateCallback: OnHDInterOpStateChanged = { state: HDInterOpState ->
        mqttInfo("$TAG::init connectStateChannel receiver : $state")
        mqttInfo("$TAG::init connectStateChannel cur      : ${this@InterOpHDMqttClientImpl.reference}")
        if (state.reference == this@InterOpHDMqttClientImpl.reference) {
            val newInternalState =
                if (state.connect) HDMqttState.Connected else HDMqttState.Disconnected
            internalState = newInternalState
            onStateChangedListener?.onChanged(newInternalState)
        } else {
            mqttWarn("$TAG::init connectStateChannel 不是当前client")
        }
    }

    override fun init() {
        mqttInfo("$TAG::init inOpt:$inOpt")
        inOpt.registerMessage(messageCallback)
        inOpt.register(stateCallback)
//        inOpt.connectStateChannel.onEach {
//
//            try {
//                mqttInfo("$TAG::init ==================>")
//                mqttInfo("$TAG::init rec:${it.reference}")
//                mqttInfo("$TAG::init cur:${reference}")
//            } catch (e: Exception) {
//                mqttError("$TAG::init onEach error :$it")
//            }
//        }.onCompletion {
//            mqttError("$TAG::init onCompletion error :$it")
//        }.launchIn(coroutineScope).invokeOnCompletion {
//            mqttInfo("$TAG::init invokeOnCompletion <==")
//        }
    }

    override fun connect(param: HDMqttParam, listener: OnActionListener) {
        val host = param.shortUrl
        val port = param.port
        val clientId = param.clientId
        val username = param.username
        val password = param.password
        mqttInfo("$TAG::connect  ${outOpt.initializeMQTT}")
        val ref = outOpt.initializeMQTT?.invoke(
            host,
            port.toUInt(),
            clientId,
            username,
            password,
            ""
        )
        mqttInfo("$TAG::connect success ! ref:$ref")
        this.reference = ref ?: ""
        this._param = param
    }

    override fun subscribeTopic(
        topic: String, actionListener: OnActionListener,
    ) {
        mqttInfo("$TAG::subscribeTopic ${outOpt.subscribe}")
        val ref = reference ?: return
        outOpt.subscribe?.invoke(topic, ref)
    }

    override fun unsubscribeTopic(topic: String, listener: OnActionListener) {
        mqttInfo("$TAG::unsubscribeTopic $topic")
        val ref = reference ?: return
        outOpt.unSubscribe?.invoke(topic, ref)

    }

    override fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        retained: Boolean,
        listener: OnActionListener,
    ) {
        try {
            val ref = reference ?: return
            mqttInfo("$TAG::publish ${outOpt.publish}")
            outOpt.publish?.invoke(topic, payload.decodeToString(), ref)
            listener.onSuccess(this.hdClientId)
        } catch (e: Exception) {
            listener.onFailure(this.hdClientId, e)
        }

    }

    override fun disconnect() {
        mqttInfo("$TAG::disconnect reference:${reference} ${outOpt.disconnect}")
        val ref = reference ?: return
        mqttInfo("$TAG::disconnect 开始清理")
        outOpt.disconnect?.invoke(ref)

        coroutineScope.cancel()

        internalState = HDMqttState.Disconnected
        onStateChangedListener?.onChanged(HDMqttState.Disconnected)

        inOpt.unregister(stateCallback)
        inOpt.unregisterMessage(messageCallback)
        onStateChangedListener = null
        onMessageChangedListener = null
        _param = null
        reference = null

    }

    override val state: HDMqttState
        get() = internalState

    companion object {
        private const val TAG = "InterOpHDMqttClientImpl"
    }
}