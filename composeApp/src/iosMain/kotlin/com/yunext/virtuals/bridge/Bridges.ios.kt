package com.yunext.virtuals.bridge

import com.yunext.kmp.common.logger.HDLogger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

actual fun changeKeyBoardType(changeTo: OrientationType, isFromUser: Boolean) {
    HDLogger.d(
        "Bridges",
        "【changeKeyBoardType】设置屏幕方向${changeTo}${changeScreenOrientationFunc}"
    )
    changeScreenOrientationFunc?.invoke(changeTo.type)
    // 上一步调用ios的切换屏幕操作，不会触发onScreenChange。所以需要自己手动更新屏幕状态。
    // TODO 但是如果ios切换失败了，那下面就有问题了，需要同步ios的横竖屏状态。
    if (isFromUser) {
        updateOrientationTypeFlow(changeTo)
        //refreshOrientationType()
    }
}


/**
 * 【桥接方法】
 * ios回调
 */
var changeScreenOrientationFunc: ((to: Int) -> Unit)? = null

/**
 * 【桥接方法】
 * kotlin->swift
 */
fun changeScreenOrientation(callBack: (to: Int) -> Unit) {
    HDLogger.d("Bridges", "【changeScreenOrientation】kotlin->swift 设置ios回调${callBack}")
    changeScreenOrientationFunc = callBack
}


/**
 * 【桥接方法】
 * swift->kotlin
 * @param orientation 0:竖屏;1:横屏
 */
fun onScreenChange(orientation: Int) {
    when (orientation) {
        OrientationType.Port.type -> {
            HDLogger.d("Bridges", "【onScreenChange】竖屏")
            updateOrientationTypeFlow(OrientationType.Port)
        }

        OrientationType.Land.type -> {
            HDLogger.d("Bridges", "【onScreenChange】横屏")
            updateOrientationTypeFlow(OrientationType.Land)
        }
    }
}

//<editor-fold desc="主动获取横竖屏">

@Deprecated("测试主动获取横竖屏。能主动获取，" +
        "但是可能在swift中获取（refreshOrientationType）和设置（changeScreenOrientation） 的对象不一样")
private fun refreshOrientationType() {
    tryGetScreenOrientationFunc.invoke { orientation ->
        MainScope().launch {
            HDLogger.d("Bridges", "【tryGetScreenOrientationFunc】延迟主动获取......")
            delay(3000)
            HDLogger.d("Bridges", "【tryGetScreenOrientationFunc】主动获取$orientation")
            onScreenChange(orientation)
        }
    }

}

var tryGetScreenOrientationFunc: ((Int) -> Unit) -> Unit = { }
fun tryGetScreenOrientation(callBack: ((Int) -> Unit) -> Unit) {
    HDLogger.d(
        "Bridges",
        "【tryGetScreenOrientationFunc】kotlin->swift 设置ios[获取横竖屏状态]回调${callBack}"
    )
    tryGetScreenOrientationFunc = callBack
}
//</editor-fold>






