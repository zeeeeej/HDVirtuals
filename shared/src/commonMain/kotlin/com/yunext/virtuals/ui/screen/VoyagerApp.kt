package com.yunext.virtuals.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior


@Composable
fun VoyagerApp() {
    Navigator(
        screen = SplashScreen(),
        disposeBehavior = NavigatorDisposeBehavior(),
        onBackPressed = {
            true
        })
}
