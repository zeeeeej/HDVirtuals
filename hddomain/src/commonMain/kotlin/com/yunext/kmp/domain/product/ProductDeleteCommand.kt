package com.yunext.kmp.domain.product

import com.yunext.kmp.domain.CommandHandler
import com.yunext.kmp.domain.DomainCommand
import com.yunext.kmp.domain.DomainEventQueue

/**
 * 删除产品命令
 */
data class ProductDeleteCommand(
    val model: String,
) : DomainCommand

/**
 * * 删除产品命令处理器
 */
class ProductDeleteCommandHandler(private val productRepository: ProductRepository) :
    CommandHandler<ProductDeleteCommand> {
    override fun handle(eventQueue: DomainEventQueue, command: ProductDeleteCommand) {
        val product: Product = this.productRepository.findOrThrow(command.model)
        product.delete(eventQueue)
        productRepository.delete(product)
    }
}