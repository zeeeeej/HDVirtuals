package com.yunext.kmp.mqtt

import kotlin.native.concurrent.ThreadLocal

class HDMQTTConstant {

    @ThreadLocal
    companion object {
        var debug: Boolean = true
        internal const val TAG = "hdmqtt"
    }
}

