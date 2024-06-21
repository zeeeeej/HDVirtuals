package com.yunext.kmp.domain

interface DomainEventDispatcher<in Queue:DomainEventQueue> {
    fun dispatchNow(queue: Queue)
}

class SyncDomainEventDispatcher(
    private val listenerList: List<DomainEventListener>
):DomainEventDispatcher<SimpleEventQueue>{
    override fun dispatchNow(queue: SimpleEventQueue) {
        queue.list().forEach {
            event->
            listenerList.forEach {
                listener->
                listener.onEvent(queue,event)
            }
        }
    }

}