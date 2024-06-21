package com.yunext.kmp.domain

import com.yunext.kmp.domain.product.ProductFactory
import com.yunext.kmp.domain.product.ProductRepository
import kotlin.native.concurrent.ThreadLocal

interface DomainProvider {
    fun productRepository(): ProductRepository
    fun productFactory(): ProductFactory
}

@ThreadLocal
object DomainProvideOwner {
    private var _provider: DomainProvider? = null
    val provider: DomainProvider
        get() = _provider ?: throw DomainException("设置DomainProvider:fun provider(provider: DomainProvider)  ")

    fun provider(provider: DomainProvider) {
        this._provider = provider
    }
}

