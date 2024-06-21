package com.yunext.kmp.domain

import com.yunext.kmp.domain.product.Product
import com.yunext.kmp.domain.product.ProductRepository

class ProductRepositoryImpl : ProductRepository {
    companion object {
        private const val TAG = "【ProductRepository】"
    }

    private val list: MutableList<Product> = mutableListOf()
    override fun add(product: Product) {
        list.add(product)
        println("$TAG add end:${list.size}")
    }

    override fun findOrThrow(model: String): Product {
        return try {
            list.single { it.model == model }
        } catch (e: Throwable) {
            throw DomainException(message = null, cause = e)
        }
    }

    override fun find(model: String): Product? {
        return list.singleOrNull()
    }

    override fun delete(product: Product) {
        list.removeAll { it.model == product.model }
        println("$TAG delete end:${list.size}")
    }

    override fun update(product: Product) {
        println("$TAG update $product")
        list.removeAll { it.model == product.model }
        list.add(product)
    }


}