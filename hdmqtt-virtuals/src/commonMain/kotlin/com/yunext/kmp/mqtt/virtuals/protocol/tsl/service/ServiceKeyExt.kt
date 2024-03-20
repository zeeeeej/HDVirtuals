package com.yunext.kmp.mqtt.virtuals.protocol.tsl.service

import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.ld
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.logger
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleParsePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.Tsl
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslProperty
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslService
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslServiceCallType


fun Tsl.tslHandleTsl2ServiceKeys(): Map<String, ServiceKey> {
    logger.ld("tslHandleTsl2ServiceKeys a")
    return try {
        val services = services
        if (services.isEmpty()) return mapOf()
        val all: MutableMap<String, ServiceKey> = mutableMapOf()
        services.forEach { service ->
            val id = service.identifier
            val key = tslHandleParseServiceKey(service)
            all[id] = key
        }
        all
    } catch (e: Throwable) {
        e.printStackTrace()
        logger.ld("tslHandleTsl2ServiceKeys error $e")
        mapOf()
    } finally {
        logger.ld("tslHandleTsl2ServiceKeys z")
    }
}

private fun tslHandleParseServiceKey(
    tslEvent: TslService
): ServiceKey {
    val type = tslEvent.callType
    return when (TslServiceCallType.from(type)) {
        TslServiceCallType.ASYNC -> AsyncEventKey(
            identifier = tslEvent.identifier,
            name = tslEvent.name,
            required = tslEvent.required,
            desc = tslEvent.desc,
            method = tslEvent.desc,
            inputData = tslEvent.inputData.map {
                val tslProperty = TslProperty.from(it)
                tslHandleParsePropertyKey(tslProperty)
            }.filterNotNull(),
            outputData = tslEvent.outputData.map {
                val tslProperty = TslProperty.from(it)
                tslHandleParsePropertyKey(tslProperty)
            }.filterNotNull()
        )
        TslServiceCallType.SYNC -> SyncEventKey(
            identifier = tslEvent.identifier,
            name = tslEvent.name,
            required = tslEvent.required,
            desc = tslEvent.desc,
            method = tslEvent.desc,
            inputData = tslEvent.outputData.map {
                val tslProperty = TslProperty.from(it)
                tslHandleParsePropertyKey(tslProperty)
            }.filterNotNull(),
            outputData = tslEvent.outputData.map {
                val tslProperty = TslProperty.from(it)
                tslHandleParsePropertyKey(tslProperty)
            }.filterNotNull()
        )
    }


}