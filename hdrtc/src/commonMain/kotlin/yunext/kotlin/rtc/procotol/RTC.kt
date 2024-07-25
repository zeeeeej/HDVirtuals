package yunext.kotlin.rtc.procotol


internal val rtcScope by lazy {
    RTCScopeImpl()
}

 const val RTC_ACCESS_KEY = "12xs8gmkwgrgxtye"
fun <R> rtc(block: RTCScope.() -> R): R {
    return block(rtcScope)
}

fun <T, R> rtc(t: T, block: RTCScope.(T) -> R): R {
    return block(rtcScope, t)
}

fun <T1, T2, R> rtc(t1: T1, t2: T2, block: RTCScope.(T1, T2) -> R): R {
    return block(rtcScope, t1, t2)
}

fun <T1, T2, T3, R> rtc(t1: T1, t2: T2, t3: T3, block: RTCScope.(T1, T2, T3) -> R): R {
    return block(rtcScope, t1, t2, t3)
}
