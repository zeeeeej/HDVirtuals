package com.yunext.kmp.common.util

actual fun hdMD5(text: String, upperCase: Boolean): String? {
    return "hdMD5_ios_error"
}

private const val BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

actual fun hdRandomString(length: Int): String {
    require(length > 0) {
        "hdRandomString length must > 0"
    }
    return List(length) {
        BASE.random().toString()
    }.joinToString { it }
}