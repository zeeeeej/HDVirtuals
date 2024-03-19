package com.yunext.kmp.mqtt.core

interface HDMqttActionListener {
    fun onSuccess(token: Any?)
    fun onFailure(token: Any?, exception: Throwable?)
}

fun interface MQTTSuccessActionListener{
    fun onSuccess(token: Any)
}

fun interface MQTTFailureActionListener{
    fun onFailure(token: Any, exception: Throwable)
}