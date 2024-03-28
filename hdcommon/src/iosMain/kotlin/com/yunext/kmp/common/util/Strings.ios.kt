package com.yunext.kmp.common.util

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.HDBridge
import korlibs.crypto.MD5
import korlibs.crypto.hash
import kotlin.native.concurrent.ThreadLocal

actual fun hdMD5(text: String, upperCase: Boolean): String? {
    return text.encodeToByteArray().hash(MD5).hexLower
}

private fun md5Internal(source: String, callback: (String) -> Unit) {
    HDMd5.bridgeMd5Callback?.invoke(source) {
        callback.invoke(it)
    }
}

@ThreadLocal
object HDMd5 : HDBridge {


    var bridgeMd5Callback: ((String, (String) -> Unit) -> Unit)? = null
    fun bridgeMd5(callBack: (String, (String) -> Unit) -> Unit) {
        HDLogger.d(
            "Bridges",
            "【tryGetScreenOrientationFunc】kotlin->swift 设置ios[获取横竖屏状态]回调${callBack}"
        )
        this.bridgeMd5Callback = callBack
    }
}


private const val BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

actual fun hdUUID(length: Int): String {
    require(length > 0) {
        "hdRandomString length must > 0"
    }
    return List(length) {
        BASE.random().toString()
    }.joinToString("") { it }
}