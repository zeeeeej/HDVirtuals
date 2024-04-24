package com.yunext.virtuals.ui.screen.devicedetail.tabnormal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.yunext.virtuals.ui.common.TwinsEmptyView
import com.yunext.virtuals.ui.screen.devicedetail.ListTslService

class DeviceDetailSubServiceScreen : Screen {

    @Composable
    override fun Content() {
        DeviceDetailSubServiceScreenImpl()
    }
}

@Composable
internal fun DeviceDetailSubServiceScreenImpl() {
    Box(Modifier.fillMaxSize()) {
        //HDDebugText("设备详情-服务")

        val list by remember {
            mutableStateOf(List(20) { it })
        }
        if (list.isEmpty()) {
            TwinsEmptyView()
        } else {
            Box(Modifier.padding(horizontal = 16.dp)) {
                ListTslService(list = list) {

                }
            }
        }
    }
}