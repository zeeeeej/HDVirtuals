package com.yunext.kmp.mqtt.core

import com.yunext.kmp.context.HDContext
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.interop.HDCocoaMQTTInterOpIn
import com.yunext.kmp.mqtt.interop.HDCocoaMQTTInterOpOut
import com.yunext.kmp.mqtt.utils.mqttInfo
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class InterOpHDMqttClientImpl(hdContext: HDContext) : IHDMqttClient {
    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("KMQTTClient"))

    // kotlin -> Swift
    private val outOpt = HDCocoaMQTTInterOpOut
    // kotlin <- Swift
    private val inOpt = HDCocoaMQTTInterOpIn
    //

    private var reference: String? = null
    private var inOptJob: Job? = null
    override val tag: String
        get() = "tag-swift-interop"

    private var internalState: HDMqttState = HDMqttState.Init

    private var onStateChangedListener: OnStateChangedListener? = null
    private var onMessageChangedListener: OnMessageChangedListener? = null
    internal fun registerOnStateChangedListener(listener: OnStateChangedListener) {
        onStateChangedListener = listener
    }

    internal fun registerOnMessageChangedListener(listener: OnMessageChangedListener) {
        onMessageChangedListener = listener
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun init() {
        mqttInfo("mqtt-ios-init")
        inOptJob?.cancel()
        inOptJob = null
        inOptJob = coroutineScope.launch {
            inOpt.messageChannel.receiveAsFlow().onEach {
                mqttInfo("mqtt-ios-init inOpt receiver message $it")
                if (it.reference == this@InterOpHDMqttClientImpl.reference) {
                    onMessageChangedListener?.onChanged(
                        it.topic, it.message
                    )
                }
            }.onCompletion {
                mqttInfo("mqtt-ios-init inOpt messageChannel error $it")
            }.launchIn(this)

            inOpt.connectStateChannel.receiveAsFlow().onEach {
                mqttInfo("mqtt-ios-init inOpt receiver message $it")
                if (it.reference == this@InterOpHDMqttClientImpl.reference) {
                    val newInternalState =
                        if (it.connect) HDMqttState.Connected else HDMqttState.Disconnected
                    internalState = newInternalState
                    onStateChangedListener?.onChanged(newInternalState)
                }
            }.onCompletion {
                mqttInfo("mqtt-ios-init inOpt connectStateChannel error $it")
            }.launchIn(this)
        }
    }

    override fun connect(param: HDMqttParam, listener: OnActionListener) {
        val host = param.shortUrl
        val port = param.port
        val clientId = param.clientId
        val username = param.username
        val password = param.password
        val reference = "${clientId}-${username}"
        mqttInfo("mqtt-ios-connect ${outOpt.initializeMQTT}")
        val ref = outOpt.initializeMQTT?.invoke(
            host,
            port.toUInt(),
            clientId,
            username,
            password,
            reference
        ) // clientId + username
        mqttInfo("mqtt-ios-connect ref:$ref")
        this.reference = ref ?: ""
    }

    override fun subscribeTopic(
        topic: String, actionListener: OnActionListener,
    ) {
        mqttInfo("mqtt-ios-subscribeTopic ${outOpt.subscribe}")
        val ref = reference ?: return
        outOpt.subscribe?.invoke(topic, ref)
    }

    override fun unsubscribeTopic(topic: String, listener: OnActionListener) {
        mqttInfo("mqtt-ios-unsubscribeTopic")
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
        mqttInfo("mqtt-ios-publish ${outOpt.publish}")
        val ref = reference ?: return
        outOpt.publish?.invoke(topic, payload.decodeToString(), ref)
    }

    override fun disconnect() {
        mqttInfo("mqtt-ios-disconnect ${outOpt.disconnect}")
        val ref = reference ?: return
        outOpt.disconnect?.invoke(ref)
        inOptJob?.cancel()
        inOptJob = null
        onStateChangedListener = null
        onMessageChangedListener = null
        coroutineScope.cancel()
    }

    override val state: HDMqttState
        get() = internalState
}