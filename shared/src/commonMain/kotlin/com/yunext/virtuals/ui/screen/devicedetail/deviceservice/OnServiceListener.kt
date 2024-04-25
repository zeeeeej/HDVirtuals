package com.yunext.virtuals.ui.screen.devicedetail.deviceservice

import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.virtuals.ui.data.EventData
import com.yunext.virtuals.ui.data.ServiceData

typealias OnServiceListener = (ServiceData,List<PropertyValue<*>>) -> Unit
typealias OnEventListener = (eventKey: EventData, List<PropertyValue<*>>) -> Unit
typealias OnPropertyListener = (PropertyValue<*>) -> Unit