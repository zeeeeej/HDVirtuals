package com.yunext.kmp.domain.product

import com.yunext.kmp.domain.DomainEventQueue
import com.yunext.kmp.domain.DomainException

class ProductFactory(private val productRepository: ProductRepository) {

    fun create(queue: DomainEventQueue, command: ProductAddCommand):Product{
        val product = this.productRepository.find(command.model)
        if (product!=null){
            throw AddProductException("已经存在model=${command.model}的产品")
        }
        val newProduct = ProductImpl(command.model,command.communicationType)
        queue.enqueue(ProductAddedEvent(model = command.model, communicationType = command.communicationType))
        return newProduct

    }
}

class AddProductException (msg:String): DomainException(msg)