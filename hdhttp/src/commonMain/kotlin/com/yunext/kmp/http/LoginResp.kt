package com.yunext.kmp.http

import com.yunext.kmp.http.core.HttpResponse

data class LoginResp(
    val token: String? = "",
    val authCode: String? = "",
) : HttpResponse