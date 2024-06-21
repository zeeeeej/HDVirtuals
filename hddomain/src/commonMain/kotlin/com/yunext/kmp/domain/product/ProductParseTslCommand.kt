package com.yunext.kmp.domain.product

import com.yunext.kmp.domain.CommandHandler
import com.yunext.kmp.domain.DomainCommand
import com.yunext.kmp.domain.DomainEventQueue

/**
 * 解析产品Tsl命令
 */
data class ProductParseTslCommand(
    val model: String,
    val json:String
) : DomainCommand

/**
 * * 解析产品Tsl命令处理器
 */
class ProductParseTslCommandHandler(private val productRepository: ProductRepository) :
    CommandHandler<ProductParseTslCommand> {
    override fun handle(eventQueue: DomainEventQueue, command: ProductParseTslCommand) {
        val product: Product = this.productRepository.findOrThrow(command.model)
        product.parseTsl(eventQueue,command.json) // 处理tsl
        productRepository.update(product) // 存储到数据库
    }
}