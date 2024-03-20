package com.yunext.virtuals.data.device

import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTContainer

class DeviceAndMessage(
    val device: MQTTDevice,
    val topic:String,
    val message: ProtocolMQTTContainer<*>?
)