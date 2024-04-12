package com.yunext.kmp.mqtt.virtuals.repository

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.resp.tsl.TslEventResp
import com.yunext.kmp.resp.tsl.TslItemPropertyResp
import com.yunext.kmp.resp.tsl.TslParamResp
import com.yunext.kmp.resp.tsl.TslPropertyResp
import com.yunext.kmp.resp.tsl.TslResp
import com.yunext.kmp.resp.tsl.TslServiceResp
import com.yunext.kmp.resp.tsl.TslSpecResp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.Tsl
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslEvent
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslItemProperty
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslParam
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslProperty
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslPropertyType
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslService
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslSpec

fun TslResp.convert() = Tsl(
    id = this.id ?: "",
    version = this.version ?: "",
    productKey = this.productKey ?: "",
    current = this.current ?: false,
    events = this.events?.map {
        it.convert()
    } ?: listOf<TslEvent>(),
    properties = this.properties?.map(TslPropertyResp::convert) ?: listOf(),
    services = this.services?.map(TslServiceResp::convert) ?: listOf(),

    )

fun TslEventResp.convert() = TslEvent(
    identifier = this.identifier ?: "",
    name = this.name ?: "",
    type = this.type ?: "",
    required = this.required ?: false,
    desc = this.desc ?: "",
    method = this.method ?: "",
    outputData = this.outputData?.map { it.converts() } ?: listOf(),
)

internal fun TslItemPropertyResp.convert() = TslItemProperty(
    identifier = identifier ?: "",
    name = name ?: "",
    dataType = TslPropertyType.from(dataType ?: ""),
    specs = specs?.convert(),
)

fun TslParamResp.converts() = this.run {
    TslParam(
        identifier = this.identifier ?: "",
        name = this.name ?: "",
        dataType = this.dataType ?: "",
        specs = this.specs?.convert()
    )
}

fun TslPropertyResp.convert() = TslProperty(
    accessMode = accessMode ?: "",
    required = required ?: false,
    desc = desc ?: "",
    identifier = identifier ?: "",
    name = name ?: "",
    dataType = TslPropertyType.from(dataType ?: ""),
    specs = specs?.convert(),
)

fun TslServiceResp.convert() = TslService(
    identifier = this.identifier ?: "",
    name = this.name ?: "",
    callType = this.callType ?: "",
    required = this.required ?: false,
    desc = this.desc ?: "",
    method = this.method ?: "",
    inputData = this.inputData?.map(TslParamResp::converts) ?: listOf(),
    outputData = this.outputData?.map(TslParamResp::converts) ?: listOf(),
)

fun TslSpecResp.convert() = this.run {
    HDLogger.d("converts", "TslSpecResp::convert ${this.enumDesc}")
    TslSpec(
        min = this.min,
        max = this.max,
        unit = this.unit,
        unitName = this.unitName,
        size = this.size,
        step = this.step,
        length = this.length,
        type = this.type,
        item = this.item,
        enumDesc =  this.enumDesc
    )
}
