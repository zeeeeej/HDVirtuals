package com.yunext.kmp.resp


data class LoginResp(
    val token: String? = "",
    val authCode: String? = "",
)