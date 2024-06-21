package com.yunext.kmp.domain

/**
 * 命令处理器
 */
internal interface CommandHandler<Command : DomainCommand> {
    /**
     * [eventQueue] 事件队列
     * [command] 命令
     */
    fun handle(eventQueue: DomainEventQueue, command: Command)
}