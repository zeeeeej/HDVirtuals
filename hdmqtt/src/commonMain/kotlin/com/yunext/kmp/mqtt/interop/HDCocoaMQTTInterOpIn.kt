package com.yunext.kmp.mqtt.interop

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.data.HDMqttMessage
import kotlinx.coroutines.channels.Channel
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

object HDCocoaMQTTInterOpIn {


    private val connectStateChannelInternal: Channel<HDInterOpState> = Channel()
    private val messageChannelInternal: Channel<HDInterOpMqttMessage> = Channel()

    val connectStateChannel: Channel<HDInterOpState>
        get() = connectStateChannelInternal

    val messageChannel: Channel<HDInterOpMqttMessage>
        get() = messageChannelInternal

    //<editor-fold desc="swift call kotlin">
    // NOTICE : DO NOT CALL IN KOTLIN !!!
    fun onConnectStateChangedCallInSwift(reference: String, connect: Boolean) {
        connectStateChannelInternal.trySend(HDInterOpState(reference, connect))
    }

    fun onMessageArrivedCallInSwift(
        reference: String,
        topic: String,
        qos: Int,
        payload: ByteArray,
        retained: Boolean,
        dup: Boolean,
    ) {

//        val temp = payload.map { it.toByte() }
       HDLogger.d("onMessageArrivedCallInSwift","size:${payload.size},msg:{$payload}")
        val message = HDMqttMessage(
            qos = qos,
            mutable = false,
            retained = retained,
            payload = payload, messageId = 0, dup = dup
        )

        messageChannelInternal.trySend(
            HDInterOpMqttMessage(
                reference = reference,
                topic = topic,
                message = message
            )
        )
    }
    //</editor-fold>


}