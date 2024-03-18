package com.yunext.virtuals.bridge

import android.content.pm.ActivityInfo
import com.yunext.kmp.context.hdContext

/**
 * 切换
 */
actual fun changeKeyBoardType(changeTo: OrientationType, isFromUser: Boolean) {
    if (!isFromUser) return
    val activity = hdContext.topActivity?:return
    activity.requestedOrientation =
        when(changeTo){
            OrientationType.Port -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            OrientationType.Land ->  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
}