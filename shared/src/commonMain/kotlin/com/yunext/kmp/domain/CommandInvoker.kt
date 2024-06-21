package com.yunext.kmp.domain

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface CommandInvoker {
    suspend operator fun <R, Queue> invoke(block: (DomainEventQueue) -> R): R where  Queue : DomainEventQueue
}

class OneTransactionCommandInvoker(
    private val domainEventDispatcher: DomainEventDispatcher<SimpleEventQueue>,
) : CommandInvoker {
    override suspend fun <R, Queue> invoke(block: (DomainEventQueue) -> R): R where  Queue : DomainEventQueue {
        return suspendCancellableCoroutine { continuation ->
            try {
                val eventQueue = SimpleEventQueue()
                val result = block.invoke(eventQueue)
                this.domainEventDispatcher.dispatchNow(eventQueue)
                continuation.resume(result)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    companion object {

    }

}