package com.yunext.kmp.domain.product

interface Tsl {
    val source: String
    val version: String
    val propertyList: List<TslProperty>
    val eventList: List<TslEvent>
    val serviceList: List<TslService>
}

data class TslImpl(
    override val source: String = "",
    override val version: String = "",
    override val propertyList: List<TslProperty> = listOf(),
    override val eventList: List<TslEvent> = listOf(),
    override val serviceList: List<TslService> = listOf(),
) : Tsl

interface TslProperty
interface TslEvent
interface TslService