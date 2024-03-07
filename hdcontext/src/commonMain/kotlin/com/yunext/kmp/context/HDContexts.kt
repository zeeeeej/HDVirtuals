package com.yunext.kmp.context

val hdContext: HDContext = HDContext()

fun initHDContext(block: HDContext.() -> Unit) {
    try {
        hdContext.block()
    } catch (e: Throwable) {
        throw HDContextException(message = null, cause = e)
    }
}