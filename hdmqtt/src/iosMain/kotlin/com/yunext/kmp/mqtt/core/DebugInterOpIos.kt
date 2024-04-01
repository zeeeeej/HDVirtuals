package com.yunext.kmp.mqtt.core

import com.yunext.kmp.mqtt.interop.InterOp_Out_ByteArray
import com.yunext.kmp.mqtt.utils.mqttInfo
import platform.Foundation.NSData

typealias InterOp_Out_ByteArray = (data: NSData) -> Unit

object DebugInterOpIos {



    var interOp_Out_ByteArray: InterOp_Out_ByteArray? = null
        private set

    fun interOp_Out_ByteArray(callback: InterOp_Out_ByteArray) {
        this.interOp_Out_ByteArray = callback
    }

    fun clear() {
        this.interOp_Out_ByteArray = null
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun interOp_Debug_ByteArray(data: ByteArray) {
        mqttInfo("----- debug -----")
        mqttInfo("size  : ${data.size}")
        mqttInfo("str   : ${data.decodeToString()}")
        mqttInfo("hex   : ${data.toHexString()}")
    }

    @OptIn(ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class)
    fun interOp_Debug_ByteArray2(data: UByteArray) {
        mqttInfo("----- debug2 -----")
        mqttInfo("size  : ${data.size}")
        mqttInfo("str   : ${data.toByteArray().decodeToString()}")
        mqttInfo("hex   : ${data.toByteArray().toHexString()}")
    }


}