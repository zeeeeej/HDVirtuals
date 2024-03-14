package com.yunext.virtuals.data.device

import com.yunext.kmp.mqtt.protocol.ProtocolMQTTContainer

class DeviceAndMessage(
    val device: MQTTDevice,
    val topic:String,
    val message: ProtocolMQTTContainer<*>?
)