package com.yunext.kmp.mqtt.virtuals.protocol

import kotlinx.serialization.json.Json

internal val hdJson: Json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true

}