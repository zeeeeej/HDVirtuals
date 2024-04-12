package com.yunext.kmp.resp.tsl

import kotlinx.serialization.Serializable

@Serializable
data class TslContainerResp(
    val code: Int?,
    val msg: String?,
    val success: Boolean?,
    val data: TslResp?
)

val TslContainerResp.display:String
    get() {
        return """
            // TslContainerResp
            code    :  $code
            msg     :  $msg
            success :  $success
            data    :  $data
        """.trimIndent()
    }