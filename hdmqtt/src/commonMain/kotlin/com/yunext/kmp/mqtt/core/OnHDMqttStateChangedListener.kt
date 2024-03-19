package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.data.HDMqttState

 fun interface OnHDMqttStateChangedListener {

    fun onChanged(mqttClient: HDMqttClient, mqttState: HDMqttState)
}