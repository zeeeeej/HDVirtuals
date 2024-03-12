package com.yunext.virtuals.ui.demo.voyager

import androidx.compose.runtime.Composable
import com.yunext.virtuals.ui.demo.voyager.basicNavigationScreen.BasicNavigationScreen
import com.yunext.virtuals.ui.demo.voyager.bottomSheetNavigation.BottomSheetNavigationScreen
import com.yunext.virtuals.ui.demo.voyager.nestedNavigation.NestedNavigationScreen
import com.yunext.virtuals.ui.demo.voyager.tabNavigation.TabNavigationScreen

@Composable
fun VoyagerDemoApp() {
    BasicNavigationScreen()
//    BottomSheetNavigationScreen()
//    KoinScreen()
//    TabNavigationScreen()
    NestedNavigationScreen()
}