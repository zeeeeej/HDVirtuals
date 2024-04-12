package com.yunext.kmp.resp.tsl

import com.yunext.kmp.resp.http.HttpResponse
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
class TslResp(
    val id: String?,
    @Required
    val version: String?,
    val productKey: String?,
    val current: Boolean?,
    val events: List<TslEventResp>?,
    val properties: List<TslPropertyResp>?,
    val services: List<TslServiceResp>?,
): HttpResponse

val TslResp.display: String
    get() {
        return """
            // TslResp
            id          :  $id
            version     :  $version
            productKey  :  $productKey
            current     :  $current
            events      :  ${events?.size}
            properties  :  ${properties?.size}
            services    :  ${services?.size}
        """.trimIndent()
    }

