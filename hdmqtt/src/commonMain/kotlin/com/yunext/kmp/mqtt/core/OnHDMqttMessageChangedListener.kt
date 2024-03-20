package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.HDMqttClient
import com.yunext.kmp.mqtt.data.HDMqttMessage

fun interface OnHDMqttMessageChangedListener {
    fun onChanged(mqttClient: HDMqttClient, topic: String, message: HDMqttMessage)
}

internal fun interface OnMessageChangedListener {
    fun onChanged(topic: String, message: HDMqttMessage)
}

