package com.yunext.kmp.domain.product

import com.yunext.kmp.domain.CommandHandler
import com.yunext.kmp.domain.DomainCommand
import com.yunext.kmp.domain.DomainEventQueue

/**
 * 添加产品
 */
data class ProductAddCommand(
    val model: String,
    val communicationType: CommunicationType,
    val tsl: Tsl?,
) : DomainCommand

/**
 * 添加产品处理器
 */
class ProductAddCommandHandler(private val productRepository: ProductRepository,
                               private  val factory  :ProductFactory
    ) :
    CommandHandler<ProductAddCommand> {


    override fun handle(eventQueue: DomainEventQueue, command: ProductAddCommand) {
        val product: Product = this.factory.create(eventQueue,command)
        productRepository.add(product)
    }
}