package com.yunext.kmp.http.core

import com.yunext.kmp.resp.http.HttpResponse

/* Http接口返回格式比较固定，采用以下方式解析 */

// 接口返回的基本数据
// code msg success
interface HttpResponseContainer

// 接口数据
// interface HttpResponse

// 解析器
interface HttpResponseParser<in Container : HttpResponseContainer> {
    // 解析出HttpResult<HttpResponse>
    fun <RESP : HttpResponse> parse(container: Container): HDResult<RESP>

    // 解析出HttpResult<Boolean>
    fun parseToBoolean(container: Container): HDResult<Boolean>
}

// 解析数据过程
suspend fun <Container : HttpResponseContainer, RESP : HttpResponse> HttpResponseParser<Container>.suspendParse(
    block: suspend () -> Container,
): HDResult<RESP> {
    return try {
        parse<RESP>(block())
    } catch (e: Throwable) {
        httpFail(e)
    }
}

// 解析数据+转换数据
fun <Container : HttpResponseContainer, RESP : HttpResponse, DATA> HttpResponseParser<Container>.parseToData(
    container: Container,
    map: (RESP) -> DATA
): HDResult<DATA> {
    return parse<RESP>(container).map(map)
}

// 解析数据过程+转换数据
suspend fun <Container : HttpResponseContainer, RESP : HttpResponse, DATA> HttpResponseParser<Container>.suspendParseToData(
    map: (RESP) -> DATA,
    block: suspend () -> Container
): HDResult<DATA> {
    return try {
        parseToData<Container, RESP, DATA>(block(), map)
    } catch (e: Throwable) {
        httpFail(e)
    }
}

// 解析数据过程+转换数据Boolean
suspend fun <Container : HttpResponseContainer> HttpResponseParser<Container>.suspendParseToBoolean(
    block: suspend () -> Container
): HDResult<Boolean> {
    return try {
        parseToBoolean(block())
    } catch (e: Throwable) {
        httpFail(e)
    }
}


suspend fun <T, R> parseData(
    convert: (T) -> R, block: suspend () -> T
): HDResult<R> {
    return try {
        httpSuccess(convert(block()))
    } catch (e: Throwable) {
        httpFail(e)
    }
}









