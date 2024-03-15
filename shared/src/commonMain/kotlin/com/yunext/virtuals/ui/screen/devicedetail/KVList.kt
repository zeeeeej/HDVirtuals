package com.yunext.virtuals.ui.screen.devicedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.virtuals.ui.common.TwinsEmptyView

@Composable
internal fun <T> KVList(
    tab: HDDeviceTab, list: List<T>,
    onPropertyAction: (T) -> Unit,
    onServiceAction: (T) -> Unit,
    onEventAction: (T) -> Unit,
) {
    if (list.isEmpty()) {
        TwinsEmptyView()
    } else {
        Box(Modifier.padding(horizontal = 16.dp)) {
            when (tab) {
                PropertiesTab -> {

                    ListTslProperty(list = list) {
                        onPropertyAction.invoke(it)
                    }
                }

                EventsTab -> {

                    ListTslEvent(list = list) {
                        onEventAction(it)
                    }
                }

                ServicesTab -> {

                    ListTslService(list = list) {
                        onServiceAction(it)
                    }
                }
            }
        }
    }
}