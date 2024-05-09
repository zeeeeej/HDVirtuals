package com.yunext.kmp.common.logger

abstract class HDLoggerImpl(private val delegate: HDLogger) : HDLogger {
    override var debug: Boolean
        get() = delegate.debug
        set(value) {
            delegate.debug = value
        }

    override fun d(tag: String, msg: String) {
        if (!debug) return
        delegate.d(tag, msg)
    }

    override fun i(tag: String, msg: String) {
        if (!debug) return
        delegate.i(tag, msg)
    }

    override fun w(tag: String, msg: String) {
        if (!debug) return
        delegate.w(tag, msg)
    }

    override fun e(tag: String, msg: String) {
        if (!debug) return
        delegate.e(tag, msg)
    }


}

expect interface HDLogger {
    var debug: Boolean
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