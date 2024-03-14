package com.yunext.virtuals.ui.screen.devicedetail

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.data.DeviceAndState

data class DeviceDetailScreen(private val deviceAndState: DeviceAndState) :Screen {


    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize().hdBackground()) {
            HDDebugText("设备详情:$deviceAndState")
        }
    }
}