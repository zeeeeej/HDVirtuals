package com.yunext.virtuals.ui.screen.devicedetail

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.yunext.kmp.ui.compose.hdBackground

class DeviceDetailSubPropertyScreen :Screen {


    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize().hdBackground()) {
            HDDebugText("设备详情-属性")
        }
    }
}