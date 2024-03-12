package com.yunext.kmp.common

internal data class Rect2(val a: Int, val b: Int)

internal operator fun Rect2?.plus(rect: Rect2?) = Rect2((this?.a ?: 0) + (rect?.a ?: 0), (this?.b ?: 0) + (rect?.b ?: 0))