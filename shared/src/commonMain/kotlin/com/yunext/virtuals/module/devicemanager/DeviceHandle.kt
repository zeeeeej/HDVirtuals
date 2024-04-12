package com.yunext.virtuals.module.devicemanager

import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTContainer


interface DeviceHandle {
    fun handle(deviceStore: DeviceStore, message: ProtocolMQTTContainer<*>):Boolean
}