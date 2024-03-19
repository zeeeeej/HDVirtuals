package com.yunext.kmp.mqtt.core

import org.eclipse.paho.client.mqttv3.MqttConnectOptions

internal fun MqttConnectOptions.defaultFactory(ssl: Boolean = true) {
    socketFactory = if (ssl) {
        if (empty) {
            DefaultSSL.defaultFactory()
        } else SSLUtils.getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, "")
    } else {
        DefaultSSL.defaultFactory()
    }
}

private const val empty = true
private const val caFilePath = "/cacert.pem"
private const val clientCrtFilePath = "/client.pem"
private const val clientKeyFilePath = "/client.key"