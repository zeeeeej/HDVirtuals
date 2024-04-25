package com.yunext.virtuals.module

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslEventType
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslServiceCallType
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.event.EventKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.BooleanPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoubleArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextEnumPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.service.ServiceKey
import com.yunext.virtuals.data.device.MQTTDevice
import com.yunext.virtuals.data.device.TwinsDevice
import com.yunext.virtuals.module.repository.DeviceDTO
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.DeviceStatus
import com.yunext.virtuals.ui.data.DeviceType
import com.yunext.virtuals.ui.data.EventData
import com.yunext.virtuals.ui.data.PropertyData
import com.yunext.virtuals.ui.data.PropertyValueWrapper
import com.yunext.virtuals.ui.data.ServiceData

fun DeviceDTO.toDeviceAndState() = DeviceAndStateViewData(
    name = this.name,
    communicationId = this.communicationId,
    model = this.model,
    status = when (this.type) {
        DeviceType.WIFI -> DeviceStatus.WiFiOffLine
        DeviceType.GPRS -> DeviceStatus.GPRSOffLine
    }, propertyList = emptyList(), eventList = emptyList(), serviceList = emptyList()
)

fun DeviceAndStateViewData.toDeviceDTO() = DeviceDTO(
    name = this.name,
    communicationId = this.communicationId,
    model = this.model,
    type = when (this.status) {
        DeviceStatus.GPRSOffLine -> DeviceType.GPRS
        DeviceStatus.GPRSOnLine -> DeviceType.GPRS
        DeviceStatus.WiFiOffLine -> DeviceType.WIFI
        DeviceStatus.WiFiOnLine -> DeviceType.WIFI
    }
)

fun DeviceDTO.toMqttDevice(): MQTTDevice {
    return TwinsDevice(
        name = this.name,
        deviceType = this.model,
        deviceId = this.communicationId,
        communicationType = this.type

    )
}


fun Map<String, PropertyValue<*>>?.toPropertyDataList(): List<PropertyData> {
    if (this.isNullOrEmpty()) return emptyList()
    return this.map { (k, v) ->
        val name: String = v.key.name
        val key: String = k
        val required: Boolean = v.key.required
        val readWrite: PropertyData.ReadWrite = when (v.key.accessMode) {
            "r" -> PropertyData.ReadWrite.R
            "rw" -> PropertyData.ReadWrite.RW
            "w" -> PropertyData.ReadWrite.W
            else -> PropertyData.ReadWrite.UnKnow
        }
        val propertyKey = v.key
        val type = propertyKey.type
        val innerType = when (propertyKey) {
            is DoubleArrayPropertyKey -> listOf(propertyKey.itemType)
            is FloatArrayPropertyKey -> listOf(propertyKey.itemType)
            is IntArrayPropertyKey -> listOf(propertyKey.itemType)
            is StructArrayPropertyKey -> listOf(propertyKey.itemType)
            is TextArrayPropertyKey -> listOf(propertyKey.itemType)
            is BooleanPropertyKey -> emptyList()
            is DatePropertyKey -> emptyList()
            is DoublePropertyKey -> emptyList()
            is IntEnumPropertyKey -> listOf(propertyKey.enumType)
            is TextEnumPropertyKey -> listOf(propertyKey.enumType)
            is FloatPropertyKey -> emptyList()
            is IntPropertyKey -> emptyList()
            is StructPropertyKey -> propertyKey.items.map { it.type }
            is TextPropertyKey -> emptyList()
        }
        val desc: String = v.key.desc
//        HDLogger.d(
//            "::toPropertyDataList", "-----------\n" + """
//            |id          :   $k
//            |type        :   $type
//            |v           :   $v
//            |v.value     :   ${
//                when (v) {
//                    is StructArrayPropertyValue -> v.value.size.toString()
//                    else -> v.displayValue
//                }
//            }
//            |v.display   :   ${v.displayValue}
//        """.trimMargin()
//        )
        PropertyData(
            name = name,
            key = key,
            required = required,
            readWrite = readWrite,
            type = type,
            innerType = innerType,
            desc = desc,
            value = PropertyValueWrapper(v)
        )
    }
}

fun Map<String, EventKey>?.toEventDataList(): List<EventData> {
    if (this.isNullOrEmpty()) return emptyList()
    return this.map { (k, v) ->
        val name: String = v.name
        val key: String = k
        val required: Boolean = v.required
        val eventType = when (v.type) {
            TslEventType.ALERT -> EventData.EventType.Alert
            TslEventType.INFO -> EventData.EventType.Info
            TslEventType.ERROR -> EventData.EventType.Fault
        }
        val output = v.outputData
        val desc = v.desc
        EventData(
            name = name,
            key = key,
            required = required,
            eventType = eventType,
            output = output,
            desc = desc
        )
    }.toList()
}

fun Map<String, ServiceKey>?.toServiceDataList(): List<ServiceData> {
    if (this.isNullOrEmpty()) return emptyList()
    return this.map { (k, v) ->
        val name: String = v.name
        val key: String = k
        val required: Boolean = v.required
        val desc = v.desc
        ServiceData(
            name = name,
            key = key,
            required = required,
            async = v.type == TslServiceCallType.ASYNC,
            output = v.outputData,
            input = v.inputData,
            desc = desc
        )
    }.toList()
}