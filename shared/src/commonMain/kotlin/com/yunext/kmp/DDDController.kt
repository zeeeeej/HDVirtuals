package com.yunext.kmp

import com.yunext.kmp.domain.CommandInvoker
import com.yunext.kmp.domain.DomainConstant
import com.yunext.kmp.domain.DomainEvent
import com.yunext.kmp.domain.DomainEventListener
import com.yunext.kmp.domain.DomainEventQueue
import com.yunext.kmp.domain.DomainProvideOwner
import com.yunext.kmp.domain.DomainProvider
import com.yunext.kmp.domain.OneTransactionCommandInvoker
import com.yunext.kmp.domain.ProductRepositoryImpl
import com.yunext.kmp.domain.SimpleEventQueue
import com.yunext.kmp.domain.SyncDomainEventDispatcher
import com.yunext.kmp.domain.product.CommunicationType
import com.yunext.kmp.domain.product.ProductAddCommand
import com.yunext.kmp.domain.product.ProductAddCommandHandler
import com.yunext.kmp.domain.product.ProductDeleteCommand
import com.yunext.kmp.domain.product.ProductDeleteCommandHandler
import com.yunext.kmp.domain.product.ProductFactory
import com.yunext.kmp.domain.product.ProductParseTslCommand
import com.yunext.kmp.domain.product.ProductParseTslCommandHandler
import com.yunext.kmp.domain.product.ProductRepository
import com.yunext.kmp.query.ProductModelDatasource
import com.yunext.kmp.query.ProductModelDatasourceImpl
import com.yunext.kmp.query.ProductQuery
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DDDController {
    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("DDDController"))
    private val productRepository = ProductRepositoryImpl()
    private val productFactory = ProductFactory(productRepository)

    init {
        DomainProvideOwner.provider(object : DomainProvider {
            override fun productRepository(): ProductRepository {
                return productRepository
            }

            override fun productFactory(): ProductFactory {
                return productFactory
            }

        })
    }

    private val _listenerList: MutableList<DomainEventListener> = mutableListOf()
    private val listenerList: List<DomainEventListener>
        get() = _listenerList
    private val dispatcher = SyncDomainEventDispatcher(listenerList)
    private val commandInvoker: CommandInvoker = OneTransactionCommandInvoker(dispatcher)
    private val productAddCommandHandler: ProductAddCommandHandler =
        ProductAddCommandHandler(productRepository, productFactory)

    private val productDeleteCommandHandler: ProductDeleteCommandHandler =
        ProductDeleteCommandHandler(productRepository)

    private val productParseTslCommandHandler: ProductParseTslCommandHandler =
        ProductParseTslCommandHandler(productRepository)

    //region query
    private val productModelDatasource :ProductModelDatasource = ProductModelDatasourceImpl()
    private val productQuery = ProductQuery(productModelDatasource)
    //endregion
    init {
        // 查询
        _listenerList.add(object : DomainEventListener {
            override fun onEvent(queue: DomainEventQueue, event: DomainEvent) {
                println("${DomainConstant.TAG}listener-01--> queue.size:${queue.list().size} event:${event}")
            }
        })

        _listenerList.add(object : DomainEventListener {
            override fun onEvent(queue: DomainEventQueue, event: DomainEvent) {
                println("${DomainConstant.TAG}listener-02--> queue.size:${queue.list().size} event:${event}")
            }
        })

        _listenerList.add(productQuery)
    }

    suspend fun addProduct() {
        commandInvoker<Unit, SimpleEventQueue> {
            productAddCommandHandler.handle(
                it, ProductAddCommand(
                    "123", CommunicationType.`4G`, null
                )
            )
        }
    }

    suspend fun deleteProduct() {
        commandInvoker<Unit, SimpleEventQueue> {
            productDeleteCommandHandler.handle(
                it, ProductDeleteCommand(
                    "123"
                )
            )
        }
    }

    suspend fun parseTsl(json:String){
        commandInvoker<Unit, SimpleEventQueue> {
            productParseTslCommandHandler.handle(
                it, ProductParseTslCommand(
                    "123",json
                )
            )
        }
    }

   suspend fun findAllProduct(){
       val list = productModelDatasource.list()
       println("所有的ProductModel:$list")
   }
}