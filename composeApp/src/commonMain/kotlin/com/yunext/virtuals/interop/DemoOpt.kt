package com.yunext.virtuals.interop

fun hdAccept(some: Any, block: (String) -> Int?) {
    println("[hdAccept] some:$some block:${block.invoke("from kotlin")}")
}

fun hdSupply(some: Any): (String) -> String? {
    return { fromIos ->
        "some :${some}  处理数据fromIos:$fromIos"
    }
}