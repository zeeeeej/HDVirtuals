package com.yunext.kmp.mqtt.utils

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.HDMQTTConstant


internal fun mqttDebug(msg: String) {
    HDLogger.d(HDMQTTConstant.TAG, msg)
}

internal fun mqttInfo(msg: String) {
    HDLogger.i(HDMQTTConstant.TAG, msg)
}

internal fun mqttError(msg: String) {
    HDLogger.e(HDMQTTConstant.TAG, msg)
}

internal fun mqttWarn(msg: String) {
    HDLogger.w(HDMQTTConstant.TAG, msg)
}