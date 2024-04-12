package com.yunext.virtuals.module.devicemanager

import com.yunext.kmp.mqtt.virtuals.protocol.tsl.event.EventKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue

interface HDWatcher {
    val eventKey: EventKey
    fun watch(properties: Map<String, PropertyValue<*>>): Pair<EventKey, List<PropertyValue<*>>>?
}

class TdsWatcher(override val eventKey: EventKey) : HDWatcher {
    override fun watch(properties: Map<String, PropertyValue<*>>): Pair<EventKey, List<PropertyValue<*>>>? {
        val iterator = properties.iterator()
        while (iterator.hasNext()) {
            val (k, v) = iterator.next()
            when (k) {
                "rawTDS" -> {
                    // 模拟tds异常时发出通知
                    try {
                        val value = v as? IntPropertyValue ?: return null
                        val tds = value.value ?: 0
                        if ((tds > 0) and (tds <= 100)) {
                            val key = eventKey.outputData.singleOrNull() { item ->
                                item.identifier == "code"
                            }
                            if (key != null) {
                                return eventKey to
                                        listOf(
                                            IntEnumPropertyValue.createValue(
                                                source = 1,
                                                key = key as IntEnumPropertyKey,
                                            )
                                        )
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return null
    }

}