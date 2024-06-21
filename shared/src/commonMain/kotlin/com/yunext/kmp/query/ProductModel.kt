package com.yunext.kmp.query

import com.yunext.kmp.domain.product.CommunicationType
import com.yunext.kmp.domain.product.Tsl

class ProductModel(
    val model: String,
    val communicationType: CommunicationType,
    val tsl: Tsl?,
) {
    override fun toString(): String {
        return """
            |{
            |    "model":${model},
            |    "communicationType":${communicationType},
            |    "tsl":${tsl}
            |}
        """.trimMargin()
    }
}

interface ProductModelDatasource {
    fun list(): List<ProductModel>

    fun add(productModel: ProductModel)

    fun delete(model: String)

    fun edit(productModel: ProductModel)

    fun find(model: String): ProductModel?
}

internal class ProductModelDatasourceImpl : ProductModelDatasource {

    companion object {
        private const val TAG = "【ProductModelDatasourceImpl】"
    }

    private val list: MutableList<ProductModel> = mutableListOf()
    override fun list(): List<ProductModel> {
        return list.toList().also {
            println("${TAG}list:$list")
        }
    }

    override fun add(productModel: ProductModel) {
        list.add(productModel)
        println("${TAG}add end! list:$list")
    }

    override fun delete(model: String) {
        list.removeAll { it.model == model }
        println("${TAG}delete end! list:$list")
    }

    override fun edit(productModel: ProductModel) {
        list.removeAll { it.model == productModel.model }
        list.add(productModel)
        println("${TAG}edit end! list:$list")
    }

    override fun find(model: String): ProductModel? {
        println("${TAG}find $model")
        return list.singleOrNull { it.model == model }
    }

}