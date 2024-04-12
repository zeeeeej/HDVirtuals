package com.yunext.kmp.http.core

sealed class HDResult<out T> {
    data class Success<T>(val data: T) : HDResult<T>()
    data class Fail(val error: Throwable) : HDResult<Nothing>()
}

fun <T> httpSuccess(data: T): HDResult<T> = HDResult.Success(data)
fun <T> httpFail(e: Throwable, code: Int = -1): HDResult<T> =
    HDResult.Fail(ApiException(code, e.message ?: "", e))

fun <T, R> HDResult<T>.map(map: (T) -> R): HDResult<R> {
    return when (this) {
        is HDResult.Fail -> this
        is HDResult.Success -> httpSuccess(map(this.data))
    }
}

fun <T> HDResult<T>.display(display: (T) -> String) = when (this) {
    is HDResult.Fail -> this.error.message
    is HDResult.Success -> display(this.data)
}