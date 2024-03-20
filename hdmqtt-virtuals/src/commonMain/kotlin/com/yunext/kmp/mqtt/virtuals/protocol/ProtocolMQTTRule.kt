package com.yunext.kmp.mqtt.virtuals.protocol

sealed class ProtocolMQTTRule {
    data object Device : ProtocolMQTTRule()
    data object Web : ProtocolMQTTRule()
    sealed class App : ProtocolMQTTRule() {
        data object Mini : App()
        data object H5 : App()
        data object Ios : App()
        data object Android : App()
    }
}