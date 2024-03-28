package com.yunext.kmp.mqtt.virtuals.coroutine

import com.yunext.kmp.mqtt.HDMqttClient
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.core.OnHDMqttMessageChangedListener
import com.yunext.kmp.mqtt.core.OnHDMqttStateChangedListener
import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.hdMqttConnect
import com.yunext.kmp.mqtt.hdMqttDisconnect
import com.yunext.kmp.mqtt.hdMqttInit
import com.yunext.kmp.mqtt.hdMqttPublish
import com.yunext.kmp.mqtt.hdMqttSubscribeTopic
import com.yunext.kmp.mqtt.hdMqttUnsubscribeTopic
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun HDMqttClient.hdMqttInitSuspend() {
    return suspendCancellableCoroutine {
        it.tryCatch {
            this.hdMqttInit()
        }
        it.invokeOnCancellation {
            // ignore
        }
    }
}


sealed interface MqttResult
class MqttResultError(val error: Throwable) : MqttResult
class MqttResultAction(val success: Boolean) : MqttResult
class MqttResultStateChanged(val client: HDMqttClient, val state: HDMqttState) : MqttResult
class MqttResultMessageChanged(
    val client: HDMqttClient,
    val topic: String,
    val message: HDMqttMessage,
) : MqttResult

fun HDMqttClient.hdMqttConnectFlow(
    param: HDMqttParam,
): Flow<MqttResult> {
    return callbackFlow<MqttResult> {
        try {
            this@hdMqttConnectFlow.hdMqttConnect(
                param = param,
                listener = object : OnHDMqttActionListener {
                    override fun onSuccess(token: Any?) {
                        trySend(MqttResultAction(true))
                    }

                    override fun onFailure(token: Any?, exception: Throwable?) {
                        trySend(MqttResultAction(false))
                    }

                },
                onHDMqttStateChangedListener = { client, state ->
                    trySend(MqttResultStateChanged(client, state))
                },
                onHDMqttMessageChangedListener = { mqttClient, topic, message ->
                    trySend(MqttResultMessageChanged(mqttClient, topic, message))
                })
        } catch (e: Exception) {
            trySend(MqttResultError(e))
        }

        awaitClose {
            // this@hdMqttConnectFlow.hdMqttDisconnect()
        }
    }
}

//fun HDMqttClient.hdMqttConnect(
//    param: HDMqttParam,
//    onHDMqttActionListener: OnHDMqttActionListener,
//    onHDMqttStateChangedListener: OnHDMqttStateChangedListener,
//    onHDMqttMessageChangedListener: OnHDMqttMessageChangedListener,
//) {
//    try {
//        hdMqttConnect(
//            param = param,
//            listener = object : OnHDMqttActionListener {
//                override fun onSuccess(token: Any?) {
//                    onHDMqttActionListener.onSuccess(token)
//                }
//
//                override fun onFailure(token: Any?, exception: Throwable?) {
//                    onHDMqttActionListener.onFailure(token,exception)
//                }
//
//            },
//            onHDMqttStateChangedListener = { client, state ->
//                onHDMqttStateChangedListener.onChanged(client,state)
//            },
//            onHDMqttMessageChangedListener = { mqttClient, topic, message ->
//                onHDMqttMessageChangedListener.onChanged(mqttClient, topic, message)
//            })
//    } catch (e: Exception) {
//        throw e
//    }
//
//}

suspend fun HDMqttClient.hdMqttSubscribeTopicSuspend(
    topic: String,
): MqttResultAction {
    return suspendCancellableCoroutine {
        try {
            hdMqttSubscribeTopic(topic, object : OnHDMqttActionListener {
                override fun onSuccess(token: Any?) {
                    it.resume(MqttResultAction(true))
                }

                override fun onFailure(token: Any?, exception: Throwable?) {
                    it.resume(MqttResultAction(false))
                }
            })
        } catch (e: Exception) {
            it.resumeWithException(e)
        }
        it.invokeOnCancellation {
            // ignore
        }
    }

}

suspend fun HDMqttClient.hdMqttUnsubscribeTopicSuspend(
    topic: String,
): MqttResultAction {
    return suspendCancellableCoroutine {
        try {
            hdMqttUnsubscribeTopic(topic, object : OnHDMqttActionListener {
                override fun onSuccess(token: Any?) {
                    it.resume(MqttResultAction(true))
                }

                override fun onFailure(token: Any?, exception: Throwable?) {
                    it.resume(MqttResultAction(false))
                }
            })
        } catch (e: Exception) {
            it.resumeWithException(e)
        }
        it.invokeOnCancellation {
            // ignore
        }
    }
}

suspend fun HDMqttClient.hdMqttPublishSuspend(
    topic: String,
    payload: ByteArray,
    qos: Int,
    retained: Boolean,
): MqttResultAction {
    return suspendCancellableCoroutine {
        try {
            hdMqttPublish(topic, payload, qos, retained, object : OnHDMqttActionListener {
                override fun onSuccess(token: Any?) {
                    it.resume(MqttResultAction(true))
                }

                override fun onFailure(token: Any?, exception: Throwable?) {
                    it.resume(MqttResultAction(false))
                }
            })
        } catch (e: Exception) {
            it.resumeWithException(e)
        }
        it.invokeOnCancellation {
            // ignore
        }
    }
}

suspend fun HDMqttClient.hdMqttDisconnectSuspend() {
    return suspendCancellableCoroutine {
        try {
            hdMqttDisconnect()
            it.resume(Unit)
        } catch (e: Exception) {
            it.resumeWithException(e)
        }
        it.invokeOnCancellation {
            // ignore
        }
    }
}


private fun <T> Continuation<T>.tryCatch(block: () -> T) {
    try {
        this.resume(block())
    } catch (e: Throwable) {
        this.resumeWithException(e)
    }
}
