package com.yunext.virtuals.ui.screen.setting

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import com.yunext.kmp.ui.compose.hdBackground

class SettingScreen : Screen {


    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize().hdBackground()) {
            HDDebugText("设置")
        }
    }
}