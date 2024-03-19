package com.yunext.kmp.mqtt.core

import java.security.cert.X509Certificate
import javax.net.SocketFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object DefaultSSL {

    fun defaultFactory(): SocketFactory {
        val manager =
        @Suppress("CustomX509TrustManager")
        object : X509TrustManager {
            @Suppress("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            @Suppress("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }

        val sc = SSLContext.getInstance("SSL").apply {
            init(null, arrayOf<TrustManager>(manager), null)
        }
        return sc.socketFactory
    }

}