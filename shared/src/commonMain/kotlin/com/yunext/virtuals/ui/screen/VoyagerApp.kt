package com.yunext.virtuals.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.ui.screen.rtctest.BleMenu
import com.yunext.virtuals.ui.screen.rtctest.RTCScreen


@Composable
fun VoyagerApp() {
    LaunchedEffect(Unit) {
        HDLogger.d("VoyagerApp", "::LaunchedEffect")
    }
//    val screen = RTCScreen(BleMenu.RTCTestCase,true)
    val screen = SplashScreen()
    Navigator(
        screen = screen,
        disposeBehavior = NavigatorDisposeBehavior(),
        onBackPressed = {
            HDLogger.d("VoyagerApp", "Navigator::onBackPressed $it")
            true
        })
}


@Deprecated("使用Modifier.statusBarsPadding()", ReplaceWith(".statusBarsPadding()"))
        /**
         * 适配android沉浸式
         * 参考：https://developer.aliyun.com/article/974588
         */
val LocalPaddingValues = compositionLocalOf { PaddingValues() }
