package com.yunext.kmp.mqtt.interop

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
//import platform.posix.*

typealias InterOp_Out_ByteArray_Opt = (data: NSData) -> Unit

object DebugSendByteArrayOpt {
    var interOp_Out_ByteArray: InterOp_Out_ByteArray_Opt? = null
        private set

    fun interOp_Out_ByteArray(callback: InterOp_Out_ByteArray_Opt) {
        this.interOp_Out_ByteArray = callback
    }

    fun clear() {
        this.interOp_Out_ByteArray = null
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual fun debugSendByteArray(data: ByteArray) {
    println("debugSendByteArray ios")
    data.usePinned {
        val nsData = NSData.create(bytes = it.addressOf(0), length = data.size.toULong())
        DebugSendByteArrayOpt.interOp_Out_ByteArray?.invoke(nsData)
    }
    println("debugSendByteArray ios end")

}