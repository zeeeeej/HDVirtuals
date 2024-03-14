package com.yunext.virtuals.ui.screen.logger

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import com.yunext.kmp.ui.compose.hdBackground

class LoggerScreen : Screen {

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize().hdBackground()) {
            HDDebugText("日志")
        }
    }
}