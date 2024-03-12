package com.yunext.kmp.common

import kotlin.test.Test

private data class Rect(val a: Int, val b: Int)
//private data class Rect2(val a: Int, val b: Int)

private operator fun (List<Rect>)?.plus(rect: Rect?): (List<Rect>) = listOf()
//private operator fun Rect?.plus(rect: Any?):Rect? = this?:Rect(0,0)
//private operator fun Rect?.plus(rect: Rect?) = Rect((this?.a ?: 0) + (rect?.a ?: 0), (this?.b ?: 0) + (rect?.b ?: 0))

//private operator fun Rect2?.plus(rect: Rect2?) =
//    Rect2((this?.a ?: 0) + (rect?.a ?: 0), (this?.b ?: 0) + (rect?.b ?: 0))

private fun test() {
    val c = null as? List<Rect>
    val r = (null as? List<Rect>) + null
    println("---> r = $r ")
//    val a: Rect? = null
//    val b: Rect? = null
//    val ab = a + b
}

class JvmLoggerTest {

    @Test
    fun `test 3rd element`() {

    }
}