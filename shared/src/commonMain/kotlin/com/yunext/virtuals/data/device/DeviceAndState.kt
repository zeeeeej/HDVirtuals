package com.yunext.virtuals.data.device

import com.yunext.kmp.mqtt.data.MQTTState
import com.yunext.kmp.mqtt.data.isConnected

class DeviceAndState(
    val device: MQTTDevice,
    val state: MQTTState
)

val DeviceAndState.display: String
    get() {
        return device.toString() + "\n[${
            state.isConnected.run {
                if (this) "online" else "offline"
            }
        }]"
    }