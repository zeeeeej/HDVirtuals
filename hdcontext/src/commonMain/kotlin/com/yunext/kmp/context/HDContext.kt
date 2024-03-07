package com.yunext.kmp.context

expect class HDContext constructor() {
    val context: Any
    fun init(ctx: Any)
}

