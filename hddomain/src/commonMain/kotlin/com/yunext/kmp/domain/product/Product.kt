package com.yunext.kmp.domain.product

import com.yunext.kmp.domain.DomainEvent
import com.yunext.kmp.domain.DomainEventQueue
import com.yunext.kmp.domain.DomainException

interface Product {
    val model: String
    val communicationType: CommunicationType

    val tsl: Tsl?
    fun parseTsl(eventQueue: DomainEventQueue, json: String)
    fun add(eventQueue: DomainEventQueue)
    fun delete(eventQueue: DomainEventQueue)

}

enum class CommunicationType {
    WiFi, `4G`
    ;
}

internal class ProductImpl(
    override val model: String,
    override val communicationType: CommunicationType,
    tsl: Tsl? = null,
) : Product {

    private var _tsl: Tsl? = tsl
    private var added: Boolean = false
    override val tsl: Tsl?
        get() = _tsl

    override fun parseTsl(eventQueue: DomainEventQueue, json: String) {
        // 解析tsl
        val tslTemp = TslImpl(source = json)
        // 更新聚合
        this._tsl = tslTemp
        // 发布消息
        eventQueue.enqueue(ProductTslParsedEvent(this.model, tslTemp))

    }

    override fun add(eventQueue: DomainEventQueue) {
        if (added) {
            return
        }
        this.added = true
        eventQueue.enqueue(ProductAddedEvent(this.model, this.communicationType, this.tsl))
    }

    override fun delete(eventQueue: DomainEventQueue) {
        if (!added) {
            return
        }
        this.added = false
        eventQueue.enqueue(ProductDeletedEvent(this.model))
    }

}

interface ProductRepository {
    fun add(product: Product)

    @Throws(DomainException::class)
    fun findOrThrow(model: String): Product
    fun find(model: String): Product?
    fun delete(product: Product)
    fun update(product: Product)
}

data class ProductAddedEvent(
    val model: String,
    val communicationType: CommunicationType,
    val tsl: Tsl? = null,
) : DomainEvent

data class ProductDeletedEvent(
    val model: String,
) : DomainEvent

data class ProductTslParsedEvent(
    val model: String,
    val tsl: Tsl?,
) : DomainEvent