package com.yunext.kmp.mqtt.interop


fun debugSendByteArrayWrapper(data:ByteArray){
    println("sendByteArray::start")
    println("data:${data}")
    debugSendByteArray(data)
    println("sendByteArray::end")

}

internal expect fun debugSendByteArray(data:ByteArray)