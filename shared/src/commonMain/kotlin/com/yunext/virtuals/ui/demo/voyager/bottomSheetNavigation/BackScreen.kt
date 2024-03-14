package com.yunext.virtuals.ui.demo.voyager.bottomSheetNavigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.yunext.kmp.resource.color.China
import com.yunext.kmp.ui.compose.hdBorder
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.demo.voyager.basicNavigationScreen.BasicNavigationScreen

class BackScreen : Screen {

    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().hdBorder().hdBackground { China.g_zhu_lv }
        ) {
            Button(
                onClick = { bottomSheetNavigator.show(BasicNavigationScreen(index = 0, wrapContent = true)) }
            ) {
                Text(text = "Show BottomSheet")
            }
        }
    }
}

@Composable
fun BottomSheetNavigationScreen() {
//    BottomSheetNavigator {
//        Navigator(BackScreen())
//    }
}