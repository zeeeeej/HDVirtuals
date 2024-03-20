package com.yunext.kmp.mqtt.core


interface OnHDMqttActionListener {
    fun onSuccess(token: Any?)
    fun onFailure(token: Any?, exception: Throwable?)
}

internal typealias OnActionListener = OnHDMqttActionListener