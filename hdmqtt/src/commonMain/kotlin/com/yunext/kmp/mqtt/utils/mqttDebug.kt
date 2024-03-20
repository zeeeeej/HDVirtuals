package com.yunext.kmp.mqtt.utils

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.HDMQTTConstant


internal fun mqttDebug(msg: String) {
    if (!HDMQTTConstant.debug)return
    HDLogger.d(HDMQTTConstant.TAG, msg)
}

internal fun mqttInfo(msg: String) {
    if (!HDMQTTConstant.debug)return
    HDLogger.i(HDMQTTConstant.TAG, msg)
}

internal fun mqttError(msg: String) {
    if (!HDMQTTConstant.debug)return
    HDLogger.e(HDMQTTConstant.TAG, msg)
}

internal fun mqttWarn(msg: String) {
    if (!HDMQTTConstant.debug)return
    HDLogger.w(HDMQTTConstant.TAG, msg)
}