package com.yunext.virtuals.data.device

import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTRule
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTTopic
import com.yunext.virtuals.data.ProjectInfo
import com.yunext.virtuals.data.isYinDu
import com.yunext.virtuals.module.devicemanager.DefaultMqttConvertor
import com.yunext.virtuals.module.devicemanager.DeviceInitializer
import com.yunext.virtuals.module.devicemanager.MQTTConvertor
import kotlin.reflect.KClass

interface HDDevice{
    val id:String
}

class UnSupportDeviceException(clz:KClass<*>) : RuntimeException("暂不支持的设备类型$clz")
sealed interface MQTTDevice : HDDevice {

    val rule: ProtocolMQTTRule

    val deviceType: String

    fun createMqttParam(projectInfo: ProjectInfo): HDMqttParam

    fun generateId(): String

    fun supportTopics(): Array<ProtocolMQTTTopic> =
        arrayOf(ProtocolMQTTTopic.DOWN, ProtocolMQTTTopic.QUERY)

    fun providerDeviceInitializer(): DeviceInitializer

}

fun MQTTDevice.generateTopic(projectInfo: ProjectInfo, mqttTopic: ProtocolMQTTTopic): String {
    val brand = projectInfo.brand
    val deviceID = generateId()
    if (projectInfo.isYinDu()) {
        return "/$brand/mqtt/$deviceID/${mqttTopic.category}"
    }
    return "/$brand/$deviceType/$deviceID/${mqttTopic.category}"
}

fun MQTTDevice.providerMqttConvertor(): MQTTConvertor {
    return when (this) {
        is AndroidDevice -> DefaultMqttConvertor()
        is TwinsDevice -> DefaultMqttConvertor()
        else -> throw IllegalStateException("non convertor")
    }
}







