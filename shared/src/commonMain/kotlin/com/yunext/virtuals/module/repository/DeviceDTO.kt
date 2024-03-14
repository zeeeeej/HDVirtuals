package com.yunext.virtuals.module.repository

import com.yunext.virtuals.ui.data.DeviceType

data class DeviceDTO(
    val name: String,
    val type: DeviceType,
    val communicationId: String,
    val model: String,
)

fun communicationIdAsDeviceDTO(communicationId: String) = DeviceDTO(
    name = "",
    communicationId = communicationId,
    type = DeviceType.WIFI, model = ""
)