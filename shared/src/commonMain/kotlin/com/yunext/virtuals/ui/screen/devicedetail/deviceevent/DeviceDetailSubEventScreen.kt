package com.yunext.virtuals.ui.screen.devicedetail.deviceevent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.toDefaultValue
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.StableValue
import com.yunext.virtuals.ui.common.TwinsEmptyView
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.common.stable
import com.yunext.virtuals.ui.data.EventData
import com.yunext.virtuals.ui.screen.devicedetail.bottomsheet.PropertyValueListBottomSheet

class DeviceDetailSubEventScreen : Screen {

    @Composable
    override fun Content() {

        DeviceDetailSubEventScreenImpl(emptyList()) { key, value ->
        }
    }
}

internal data class EventKeyValue(val key: EventData, val value: List<PropertyValue<*>>)

@Composable
internal fun DeviceDetailSubEventScreenImpl(
    source: List<EventData>,
    onEventTrigger: (eventKey: EventData, List<PropertyValue<*>>) -> Unit,
) {
    Debug { "[recompose_test_01] DeviceDetailSubPropertyScreenImpl size:${source.size} " }
    val list by rememberSaveable(source) {
        mutableStateOf(source)
    }

    var editingEvent: EventKeyValue? by remember { mutableStateOf(null) }
    var showTips by remember {
        mutableStateOf("")
    }

    Box(Modifier.fillMaxSize()) {
        // HDDebugText("设备详情-事件")
        if (list.isEmpty()) {
            TwinsEmptyView()
        } else {
            Box(Modifier.padding(horizontal = 16.dp)) {
                ListTslEvent(list = list) { eventData ->
                    editingEvent = EventKeyValue(key = eventData, value = eventData.output.map {
                        it.toDefaultValue()
                    })
                }
            }
        }

        onEditing(editingEvent?.stable(), onClose = {
            editingEvent = null
        }, onChanged = {
            editingEvent = null
            onEventTrigger.invoke(it.key, it.value)
        })

        if (showTips.isNotEmpty()) {
            CHLoadingDialog(showTips) {
                showTips = ""
            }
        }
    }
}

@Composable
private fun BoxScope.onEditing(
    value: StableValue<EventKeyValue>?,
    onClose: () -> Unit,
    onChanged: (EventKeyValue) -> Unit,
) {
    Debug {
        "onEditing:${value.hashCode()}"
    }
    // 属性修改
    if (value != null) {
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            PropertyValueListBottomSheet(
                value = value,
                onClose = onClose,
                onCommitted = onChanged
            )
        }
    }
}