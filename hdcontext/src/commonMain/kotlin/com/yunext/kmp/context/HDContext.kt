package com.yunext.kmp.context

import androidx.compose.runtime.staticCompositionLocalOf

expect class HDContext constructor() {
    val context: Any
    fun init(ctx: Any)
}


expect class Activity


val LocalContext = staticCompositionLocalOf {
    hdContext
}

private var currentActivity: Activity? = null

fun updateActivity(activity: Activity?) {
    currentActivity = activity
}

fun updateActivityNull() = updateActivity(null)

val LocalActivity = staticCompositionLocalOf() {
    currentActivity
}

