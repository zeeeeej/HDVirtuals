package com.yunext.kmp.domain

class SimpleEventQueue : DomainEventQueue {
    private val queue: MutableList<DomainEvent> = mutableListOf()
    override fun enqueue(event: DomainEvent) {
        queue.add(event)
    }

    override fun list(): List<DomainEvent> {
        return queue.toList()
    }
}