package com.yunext.kmp.http.tsl

import com.yunext.kmp.http.core.ApiException
import com.yunext.kmp.http.core.HDResult
import com.yunext.kmp.http.core.HttpResponseContainer
import com.yunext.kmp.http.core.HttpResponseParser
import com.yunext.kmp.resp.http.HttpResponse
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
@Serializable
class HDResponseContainer<out RESP>(
    val code: Int?,
    val msg: String?,
    val success: Boolean?,
    val data: RESP?,
) : HttpResponseContainer {
    companion object : HttpResponseParser<HDResponseContainer<*>> {
        override fun <RESP : HttpResponse> parse(container: HDResponseContainer<*>): HDResult<RESP> {
            return container.parse0()
        }

        override fun parseToBoolean(container: HDResponseContainer<*>): HDResult<Boolean> {
            return container.parseToBoolean0()
        }


        private fun <T> HDResponseContainer<*>.parse0(): HDResult<T> {
            return try {
                if (success == true) {
                    val resp = (data as T)
                    HDResult.Success(resp)
                } else {
                    HDResult.Fail(ApiException(code ?: -1, msg ?: ""))
                }
            } catch (e: Throwable) {
                HDResult.Fail(ApiException(-1, e.message ?: "", e))
            }
        }

        private fun HDResponseContainer<*>.parseToBoolean0(): HDResult<Boolean> {
            return try {
                if (success == true) {
                    HDResult.Success(true)
                } else {
                    HDResult.Success(false)
                }
            } catch (e: Throwable) {
                HDResult.Fail(ApiException(-1, e.message ?: "", e))
            }
        }
    }


}

