package com.yunext.kmp.domain

interface DomainEventQueue {
    fun enqueue(event: DomainEvent)

    fun list(): List<DomainEvent>

}