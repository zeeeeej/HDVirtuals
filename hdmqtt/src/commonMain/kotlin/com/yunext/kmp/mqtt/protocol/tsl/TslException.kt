package com.yunext.kmp.mqtt.protocol.tsl

open class TslException(
    message: String?,
    cause: Throwable? = null,
    ) : Throwable(message, cause) {
}
class TslCmdException(message: String?) : TslException(message,null)