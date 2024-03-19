package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.data.DeviceAndStateViewData

@Composable
actual fun TwinsDeviceItem(
    modifier: Modifier,
    device: DeviceAndStateViewData,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Debug("TwinsHomePage-内容-设备列表-android-TwinsDeviceItem")
    TwinsDeviceItemCommon(modifier, device, onClick, onLongClick)
}

@Composable
actual fun TwinsDeviceList(
    modifier: Modifier,
    list: List<DeviceAndStateViewData>,
    onDeviceSelected: (DeviceAndStateViewData) -> Unit,
    onDeviceDelete: (DeviceAndStateViewData) -> Unit,
) {
    Debug("TwinsHomePage-内容-设备列表-android")
    LazyColumn(
        modifier = Modifier.padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = list, key = { it.communicationId + it.model }) { device ->
            CHItemShadowShape {
                TwinsDeviceItem(modifier = Modifier.fillMaxWidth(), device = device,{
                    onDeviceSelected.invoke(device)
                }) {
                    onDeviceDelete.invoke(device)
                }
            }
        }
    }
}