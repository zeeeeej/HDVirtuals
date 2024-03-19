package com.yunext.kmp.mqtt

import com.yunext.kmp.common.util.hdUUID
import com.yunext.kmp.mqtt.data.HDMqttParam
import kotlin.native.concurrent.ThreadLocal

class HDMQTTConstant {

    @ThreadLocal
    companion object {
        var debug: Boolean = false
        internal const val PATH = "i0aa8d00.ala.cn-hangzhou.emqxsl.cn"
        internal const val PORT = "8883"
        internal const val PORT_ws = "8084"
        internal const val TAG = "_hdmqtt_"






    }
}

