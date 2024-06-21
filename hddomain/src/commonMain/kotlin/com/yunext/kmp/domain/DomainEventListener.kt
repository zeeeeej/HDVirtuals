package com.yunext.kmp.domain

/**
 * 领域事件监听器
 */
interface DomainEventListener {
    fun onEvent(queue: DomainEventQueue, event: DomainEvent)
}