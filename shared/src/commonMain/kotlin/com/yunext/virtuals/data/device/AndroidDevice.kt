package com.yunext.virtuals.data.device

import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.common.util.hdMD5
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTRule
import com.yunext.virtuals.data.ProjectInfo
import com.yunext.virtuals.module.devicemanager.DeviceInitializer

class AndroidDevice(
    private val userId: String,
    private val access: String,
    override val deviceType: String,
) : MQTTDevice {
    override val rule: ProtocolMQTTRule
        get() = ProtocolMQTTRule.App.Android

    override fun createMqttParam(projectInfo: ProjectInfo): HDMqttParam {
        val id = this.generateId()
        val clientId = "APP:${deviceType}_${id}_${currentTime()}"
        val username = id
        val passwordMd5 = hdMD5(access) ?: ""
        // TODO fill HDMqttParam port shortUrl scheme tls
        return HDMqttParam(
            username = username,
            password = passwordMd5,
            clientId = clientId,
            url = projectInfo.host, port = "", shortUrl = "", scheme = "", tls = null
        )
    }

    override fun generateId(): String {
        return userId
    }

    override fun providerDeviceInitializer(): DeviceInitializer {
        TODO("Not yet implemented")
    }

    override val id: String
        get() = userId
}