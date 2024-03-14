package com.yunext.kmp.http.core

class ApiException(val code: Int = -1, msg: String = "", cause: Throwable? = null) :
    RuntimeException(msg, cause)