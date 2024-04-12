package com.yunext.kmp.http.api

class Api {

    companion object{
        val getTsl = ApiReq(path = "/api/mqtt/device/tsl/detail",type= ReqType.GET)

        const val HOST = "farm-test.yunext.com"
        const val TOKEN = "Authorization"
    }
}

data class ApiReq(
    val path:String ,
    val type:ReqType
)


enum class ReqType{
    GET,
    POST
    ;
}