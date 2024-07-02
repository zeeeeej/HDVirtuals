package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsTitle
import com.yunext.virtuals.ui.screen.debug.DebugBluetoothLe
import org.jetbrains.compose.resources.ExperimentalResourceApi

@ExperimentalResourceApi
@Composable
internal fun RTCMasterPage(
    state: RTCMasterState,
    onLeft: () -> Unit, onStartScan: () -> Unit, onStopScan: () -> Unit,
    onConnect: (BleDeviceVo) -> Unit,
    onEnableNotify: (BleDeviceVo) -> Unit,
) {
    @OptIn(ExperimentalResourceApi::class) (TwinsBackgroundBlock(grey = true))

    Column(
        modifier = Modifier.fillMaxSize()

    ) {
        TwinsTitle(modifier = Modifier.background(Color.White), text = "Master", leftClick = {
            onLeft()
        })
        DebugBluetoothLe(
            state,
            onStartScan = onStartScan, onStopScan = onStopScan, onConnect = onConnect,onEnableNotify = onEnableNotify
        )

    }
}