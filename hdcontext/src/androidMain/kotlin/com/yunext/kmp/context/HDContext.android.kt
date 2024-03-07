package com.yunext.kmp.context

import android.app.Application
import kotlin.jvm.Throws

//public actual typealias HDContext = Application

public actual class HDContext actual constructor() {
    private lateinit var _context: Any
    actual val context: Any
        get() = _context

    actual fun init(ctx: Any) {
        if (::_context.isInitialized) {
            println("[hd]warning : HDContext::_context is already init!")
            return
        }
        _context = ctx
    }

}


val HDContext.application: Application
    // 未初始化
    @Throws(HDContextException::class)
    get() = try {
        this.context as Application
    } catch (e: Throwable) {
        throw HDContextException("context cast application fail.", cause = e)
    }