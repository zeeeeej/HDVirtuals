package com.yunext.kmp.http.datasource

import com.yunext.kmp.http.api.Api
import com.yunext.kmp.http.core.ApiException
import com.yunext.kmp.http.core.HDResult
import com.yunext.kmp.http.hdHttpClient
import com.yunext.kmp.resp.tsl.TslContainerResp
import com.yunext.kmp.resp.tsl.TslResp
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.path

class RemoteTslDatasourceImpl(private val client: HttpClient = hdHttpClient) : RemoteTslDatasource {
    override suspend fun getTsl(
        clientId: String,
        projectKey: String,
    ): HDResult<TslResp> {
        return try {
            val host = Api.HOST
            val path = Api.getTsl.path
            val resp = client.get {
                //             skipSavingBody()

                url {
                    //                protocol = URLProtocol.HTTPS
                    //                this.host = host
                    path(path)
                    //                parameters.append("a","b")
                    headers {
                        //                    append(Api.TOKEN, clientId)
                        set(Api.TOKEN, clientId)
                    }
                    //                parameter("xxx","aa")
                }
            }
            return if (resp.status.value in 200..299) {
                val result = resp.body<TslContainerResp>()
                Napier.v("json :$result")
                val tsl = result.data ?: return HDResult.Fail(ApiException(msg = "http错误"))
                HDResult.Success(tsl)
            } else {
                HDResult.Fail(ApiException(msg = "http错误"))
            }
        } catch (e: Exception) {
            HDResult.Fail(e)
        }
    }
}