package com.yunext.kmp.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

val hdHttpClient by lazy {
    HttpClient(CIO)
}