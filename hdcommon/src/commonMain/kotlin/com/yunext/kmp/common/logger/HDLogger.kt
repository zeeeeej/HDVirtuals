package com.yunext.kmp.common.logger

expect interface HDLogger {
    var debug:Boolean
    fun d(tag: String, msg: String)
    fun i(tag: String, msg: String)
    fun w(tag: String, msg: String)
    fun e(tag: String, msg: String)

    companion object : HDLogger {
        override fun d(tag: String, msg: String)

        override fun i(tag: String, msg: String)

        override fun w(tag: String, msg: String)

        override fun e(tag: String, msg: String)

    }
}