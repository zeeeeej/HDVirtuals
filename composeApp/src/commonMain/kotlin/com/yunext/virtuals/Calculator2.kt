package com.yunext.virtuals

import kotlin.experimental.ExperimentalObjCName
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.native.HidesFromObjC
import kotlin.native.ObjCName
import kotlin.native.ShouldRefineInSwift

//@Target
//annotation class Abc (val a:Int){
//    @OptIn(ExperimentalObjCRefinement::class)
//    @HidesFromObjC
//    fun a(){
//
//    }
//}

class Calculator2
constructor() {

    companion object {
        fun sum(a: Int, b: Int): Int = a + b

        @OptIn(ExperimentalObjCRefinement::class)
        // @HiddenFromObjC hides a Kotlin declaration from Objective-C and Swift. The annotation disables a function or property
        // export to Objective-C, making your Kotlin code more Objective-C/Swift-friendly.
        @HiddenFromObjC
        fun div(a: Int, b: Int) = a / b

        // @ShouldRefineInSwift帮助用Swift编写的包装器替换Kotlin声明。该注释在生成的Objective-C
        // API中将函数或属性标记为swift_private。这样的声明带有__前缀，这使得它们在Swift中不可见。
        @OptIn(ExperimentalObjCRefinement::class)
        @ShouldRefineInSwift
        fun mul(a: Int, b: Int) = a * b


        @OptIn(ExperimentalObjCName::class)
        @ObjCName(name = "mulx", swiftName = "mulx")
        fun mul2(a: Int, b: Int) = (a + b)*a
    }
}