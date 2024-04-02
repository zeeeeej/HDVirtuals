package com.yunext.kmp.mqtt.interop

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.HDMQTTConstant
import com.yunext.kmp.mqtt.data.HDMqttMessage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable

@Serializable
data class HDInterOpMqttMessage(
    val reference: String,
    val topic: String,
    val message: HDMqttMessage,

    )

@Serializable
data class HDInterOpState(
    val reference: String,
    val connect: Boolean,

    )

typealias OnHDInterOpStateChanged = (HDInterOpState) -> Unit
typealias OnHDInterOpMqttMessageChanged = (HDInterOpMqttMessage) -> Unit

object HDCocoaMQTTInterOpIn {


    private val connectStateChannelInternal: Channel<HDInterOpState> = Channel(onBufferOverflow = BufferOverflow.DROP_OLDEST)

//    val connectStateChannel: Channel<HDInterOpState>
//        get() = connectStateChannelInternal
    @Deprecated("两个client一起订阅时收不到,奇怪的")
    val connectStateChannel: Flow<HDInterOpState> = connectStateChannelInternal.receiveAsFlow()
    private val callbacks: MutableSet<OnHDInterOpStateChanged> = mutableSetOf()
    private val messageCallbacks: MutableSet<OnHDInterOpMqttMessageChanged> = mutableSetOf()

    fun register(callback: OnHDInterOpStateChanged) {
        callbacks.add(callback)
    }

    fun unregister(callback: OnHDInterOpStateChanged) {
        callbacks.remove(callback)
    }

    fun registerMessage(callback: OnHDInterOpMqttMessageChanged) {
        messageCallbacks.add(callback)
    }

    fun unregisterMessage(callback: OnHDInterOpMqttMessageChanged) {
        messageCallbacks.remove(callback)
    }

    //<editor-fold desc="swift call kotlin">
    // NOTICE : DO NOT CALL IN KOTLIN !!!
    fun onConnectStateChangedCallInSwift(reference: String, connect: Boolean) {
        trace("HDCocoaMQTTInterOpIn::onConnectStateChangedCallInSwift reference:$reference ,connect:$connect")
        connectStateChannelInternal.trySendBlocking(HDInterOpState(reference, connect))
        val iterator = callbacks.iterator()
        while (iterator.hasNext()) {
            iterator.next().invoke(HDInterOpState(reference, connect))
        }

    }

    fun onMessageArrivedCallInSwift(
        reference: String,
        topic: String,
        qos: Int,
        payload: ByteArray,
        retained: Boolean,
        dup: Boolean,
    ) {
        trace("HDCocoaMQTTInterOpIn::onMessageArrivedCallInSwift size:${payload.size},msg:${payload}")
        val message = HDMqttMessage(
            qos = qos,
            mutable = false,
            retained = retained,
            payload = payload, messageId = 0, dup = dup
        )
        val msg = HDInterOpMqttMessage(
            reference = reference,
            topic = topic,
            message = message
        )
        val iterator = messageCallbacks.iterator()
        while (iterator.hasNext()) {
            iterator.next().invoke(msg)
        }
    }
    //</editor-fold>

    private const val debug = true
    private fun trace(msg: String) {
        if (!debug) return
        HDLogger.d(HDMQTTConstant.TAG, msg)
    }

}