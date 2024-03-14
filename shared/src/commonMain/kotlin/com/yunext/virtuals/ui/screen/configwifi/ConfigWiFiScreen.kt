package com.yunext.virtuals.ui.screen.configwifi

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import com.yunext.kmp.ui.compose.hdBackground

class ConfigWiFiScreen :Screen {


    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize().hdBackground()) {
            HDDebugText("配网")
        }
    }
}