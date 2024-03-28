package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.HDMqttClient
import com.yunext.kmp.mqtt.data.HDMqttState

fun interface OnHDMqttStateChangedListener {

    fun onChanged(mqttClient: HDMqttClient, mqttState: HDMqttState)
}

internal fun interface OnStateChangedListener {

    fun onChanged(mqttState: HDMqttState)
}