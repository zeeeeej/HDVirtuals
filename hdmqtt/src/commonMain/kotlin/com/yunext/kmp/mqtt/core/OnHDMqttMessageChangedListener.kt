package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.data.HDMqttMessage

 fun interface OnHDMqttMessageChangedListener {

  fun onChanged(mqttClient: HDMqttClient, topic: String, message: HDMqttMessage)
}