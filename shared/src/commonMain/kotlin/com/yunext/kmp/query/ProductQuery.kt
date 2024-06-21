package com.yunext.kmp.query

import com.yunext.kmp.domain.DomainEvent
import com.yunext.kmp.domain.DomainEventListener
import com.yunext.kmp.domain.DomainEventQueue
import com.yunext.kmp.domain.product.ProductAddedEvent
import com.yunext.kmp.domain.product.ProductDeletedEvent
import com.yunext.kmp.domain.product.ProductTslParsedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class ProductQuery(
    private val productModelDatasource: ProductModelDatasource,
) : DomainEventListener {
    override fun onEvent(queue: DomainEventQueue, event: DomainEvent) {
        println("【ProductQuery】onEvent $event")
        when (event) {
            is ProductAddedEvent -> {
                productModelDatasource.add(
                    ProductModel(
                        model = event.model,
                        communicationType = event.communicationType,
                        tsl = event.tsl
                    )
                )
            }

            is ProductDeletedEvent -> {
                productModelDatasource.delete(event.model)
            }

            is ProductTslParsedEvent -> {
                val productModel = productModelDatasource.find(event.model) ?: return
                productModelDatasource.edit(
                    productModel = ProductModel(
                        model = productModel.model,
                        communicationType = productModel.communicationType,
                        tsl = event.tsl
                    )
                )
            }
        }
    }
}