package com.yunext.kmp.http

import com.yunext.kmp.http.api.Api
import com.yunext.kmp.http.datasource.hdJson
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.MonitoringEvent
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.parameter
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.StringValuesBuilder
import kotlinx.serialization.json.Json

val hdHttpClient by lazy {

    HttpClient() {
          // https://youtrack.jetbrains.com/issue/KTOR-5944
//        install(SaveBodyPlugin){
//
//        }

        // https://ktor.io/docs/http-client-engines.html#cio
        // this: CIOEngineConfig
//           maxConnectionsCount = 1000
//           endpoint {
//               // this: EndpointConfig
//               maxConnectionsPerRoute = 100
//               pipelineMaxSize = 20
//               keepAliveTime = 5000
//               connectTimeout = 5000
//               connectAttempts = 5
//           }

//           https {
//               // this: TLSConfigBuilder
//               serverName = "api.ktor.io"
//               cipherSuites = CIOCipherSuites.SupportedSuites
//               trustManager = myCustomTrustManager
//               random = mySecureRandom
//               addKeyStore(myKeyStore, myKeyStorePassword)
//           }
//       }

        // https://ktor.io/docs/response-validation.html#default
        expectSuccess = true
//        HttpResponseValidator {
//            validateResponse {
//                resp->
//                val json = resp.body<String>()
//                // todo
//
//            }
//        }

        install(DefaultRequest){
            url {
                protocol = URLProtocol.HTTPS
                host = Api.HOST
                //contentType(ContentType.Application.Json)
            }
        }

        // https://ktor.io/docs/client-logging.html#custom_logger
        install(Logging) {
            //logger = Logger.DEFAULT
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    trace(message)
                }
            }.also {
                Napier.base(DebugAntilog())
            }
            level = LogLevel.HEADERS
            filter { request ->
                request.url.host.contains("ktor.io")
            }

            // 去除敏感header
            //sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }

        install(TokenHeaderPlugin) {
            headerName = "custom"
            headerValue = "hadlinks"
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10_000 // 10s
            connectTimeoutMillis = 10_000 // 10s
        }

        // https://ktor.io/docs/serialization-client.html#serialization_dependency
        install(ContentNegotiation) {
//            gson(
//                contentType = ContentType.Any // workaround for broken APIs
//            )
            json(json = hdJson/*Json{
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys  =true
            }*/, contentType = ContentType.Application.Json)
            //json(contentType = ContentType.Application.Json)
        }

//        install(HttpCookies) {
//            storage = AcceptAllCookiesStorage()
//        }
//
//        install(ContentEncoding) {
//            gzip()
//            deflate()
//        }


    }
}

// https://ktor.io/docs/client-custom-plugins.html#plugin-configuration
val TokenHeaderPlugin = createClientPlugin("TokenHeaderPlugin", ::CustomHeaderPluginConfig) {
    onRequest { request, _ ->
        val headerName = this@createClientPlugin.pluginConfig.headerName
        val headerValue = this@createClientPlugin.pluginConfig.headerValue

        //request.headers.append(headerName, headerValue)
        trace("#onRequest#")
        trace("-- url     :   ${request.url}")
        trace("-- headers :   ")
        request.headers.entries().forEach {
            trace("------------   ${it.key}=>${it.value}\"")
        }
        trace("-- method  :   ${request.method}")
        //trace("-- body    :   ${request.body}")
    }

    onResponse { response ->
        trace("#onResponse#")
        trace("-- request      :   ${response.request}")
        trace("-- headers      :   ")
        response.headers.entries().forEach { (k, v) ->
            trace("-----------------   ${k}=>${v}\"")
        }
        trace("-- status       :   ${response.status}")
        //trace("-- body         :   ${response.body<String>()}")
        trace("-- requestTime  :   ${response.requestTime.timestamp}ms")
        trace("-- responseTime :   ${response.responseTime.timestamp}ms")
        trace("-- cal          :   ${response.responseTime.timestamp - response.requestTime.timestamp}ms")

    }

    onClose {
        trace("#onClose#")
    }

}

private fun Headers.display(prefix: String): String {
    return this.entries().joinToString("\n") { (k, v) -> "$prefix${k}=>${v}" }
}

private fun StringValuesBuilder.display(prefix: String): String {
    return this.entries().joinToString("\n") { (k, v) -> "$prefix${k}=>${v}" }
}

private fun trace(msg: String) {
    Napier.v(tag = "KTOR", throwable = null, message = msg)
}

class CustomHeaderPluginConfig {
    var headerName: String = "X-Custom-Header"
    var headerValue: String = "Default value"
}


// error failed with exception: kotlin.IllegalStateException: TLS sessions are not supported on Native platform
//@Deprecated("todo CIO")
//val hdHttpClientCIO by lazy {
//
//    HttpClient(CIO) {
//        // https://ktor.io/docs/http-client-engines.html#cio
//        // this: CIOEngineConfig
//        engine {
//            maxConnectionsCount = 1000
//            endpoint {
//                // this: EndpointConfig
//                maxConnectionsPerRoute = 100
//                pipelineMaxSize = 20
//                keepAliveTime = 5000
//                connectTimeout = 5000
//                connectAttempts = 5
//            }
//
//            https {
//                // this: TLSConfigBuilder
//                //serverName = "api.ktor.io"
//                //cipherSuites = CIOCipherSuites.SupportedSuites
//                //trustManager = myCustomTrustManager
//                //random = mySecureRandom
//                //addKeyStore(myKeyStore, myKeyStorePassword)
//            }
//        }
//
//
//        // https://ktor.io/docs/client-logging.html#custom_logger
//        install(Logging) {
//            //logger = Logger.DEFAULT
//            logger = object : Logger {
//                override fun log(message: String) {
//                    trace(message)
//                }
//            }.also {
//                Napier.base(DebugAntilog())
//            }
//            level = LogLevel.HEADERS
//            filter { request ->
//                request.url.host.contains("ktor.io")
//            }
//            sanitizeHeader { header -> header == HttpHeaders.Authorization }
//        }
//
//    }
//}