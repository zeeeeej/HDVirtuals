package com.yunext.virtuals.data.device

import com.yunext.kmp.mqtt.data.HDMqttState
import com.yunext.kmp.mqtt.data.isConnected

class DeviceAndState(
    val device: MQTTDevice,
    val state: HDMqttState
)

val DeviceAndState.display: String
    get() {
        return device.toString() + "\n[${
            state.isConnected.run {
                if (this) "online" else "offline"
            }
        }]"
    }