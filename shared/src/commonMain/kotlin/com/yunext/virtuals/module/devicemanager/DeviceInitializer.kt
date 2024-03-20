package com.yunext.virtuals.module.devicemanager

import com.yunext.kmp.mqtt.virtuals.protocol.tsl.Tsl
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleTsl2PropertyValues
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleUpdatePropertyValues
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleUpdatePropertyValuesFromJson


interface DeviceInitializer {
    fun init(map: Map<String, PropertyValue<*>>):Map<String, PropertyValue<*>>
}

class JsonDeviceInitializer(val json:String):DeviceInitializer{
    override fun init(map: Map<String, PropertyValue<*>>): Map<String, PropertyValue<*>> {
        return  tslHandleUpdatePropertyValuesFromJson(map, json).first
    }
}

class PropertyValuesDeviceInitializer(val  list :List<PropertyValue<*>>):DeviceInitializer{
    override fun init(map: Map<String, PropertyValue<*>>): Map<String, PropertyValue<*>> {
        return  tslHandleUpdatePropertyValues(map, list)
    }
}

class TslDeviceInitializer(val  tsl : Tsl):DeviceInitializer{
    override fun init(map: Map<String, PropertyValue<*>>): Map<String, PropertyValue<*>> {
        return  tsl.tslHandleTsl2PropertyValues()
    }
}