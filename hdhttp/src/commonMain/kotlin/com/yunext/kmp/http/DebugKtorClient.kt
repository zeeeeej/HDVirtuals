package com.yunext.kmp.http

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

suspend fun testKtor(): String {
//    val response = client.get("https://ktor.io/docs/")
//    val response = client.get("https://iot2.qinyuan.cn/web/api/common/getAdvertisement")
//    val response = hdHttpClient.get("https://www.baidu.com")
    val response =
//        hdHttpClient.get("https://iot2.qinyuan.cn/web/api/common/getFiles/64c763bd93b173b23a289558")
        hdHttpClient.get("https://ktor.io/")
    return "findAll" + "\n" + response.bodyAsText()
}