package com.yunext.kmp.mqtt

import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.utils.mqttDebug
import com.yunext.kmp.mqtt.utils.mqttError
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.android.service.MqttTraceHandler
import org.eclipse.paho.client.mqttv3.MqttMessage

private const val TEMPLATE = "[%s]%s-->[message]=%s,[error]=%s"
internal fun MqttAndroidClient.initTrace() {
    setTraceEnabled(HDMQTTConstant.debug)
    setTraceCallback(object : MqttTraceHandler {

        override fun traceDebug(tag: String?, message: String?) {
            mqttDebug(String.format(TEMPLATE, "Debug", tag ?: "-", message ?: "-", "-"))
        }

        override fun traceException(tag: String?, message: String?, e: Exception?) {
            mqttError(
                String.format(
                    TEMPLATE,
                    "Exception",
                    tag ?: "-",
                    message ?: "-",
                    e?.message ?: "-"
                )
            )
        }

        override fun traceError(tag: String?, message: String?) {
            mqttError(String.format(TEMPLATE, "Error", tag ?: "-", message ?: "-", "-"))
        }
    })
}

internal fun MqttMessage.toMsg() = HDMqttMessage(
    payload = this.payload,
    retained = this.isRetained, qos = this.qos, messageId = this.id, dup = this.isDuplicate

)