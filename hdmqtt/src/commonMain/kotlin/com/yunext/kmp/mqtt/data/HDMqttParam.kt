package com.yunext.kmp.mqtt.data

data class HDMqttParam(
    val username: String,
    val password: String,
    val clientId: String,
    val url: String = "",
    val ssl: Boolean = true,
    val port: String,
    val shortUrl: String,
    val scheme: String,
    val tls: String? = null,
)

val HDMqttParam.wholeUrl: String
    get() = "${scheme}://${shortUrl}:${port}"

val HDMqttParam.display: String
    get() = "[username]${username} ## [password]${password} ## [clientId]${clientId} ## [url]${url} ## [ssl]${ssl}"
