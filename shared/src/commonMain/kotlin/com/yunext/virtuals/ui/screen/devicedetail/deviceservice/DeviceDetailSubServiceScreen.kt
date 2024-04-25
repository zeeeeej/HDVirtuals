package com.yunext.virtuals.ui.screen.devicedetail.deviceservice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.toDefaultValue
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.TwinsEmptyView
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.common.stable
import com.yunext.virtuals.ui.data.ServiceData
import com.yunext.virtuals.ui.screen.devicedetail.BottomSheetState
import com.yunext.virtuals.ui.screen.devicedetail.bottomsheet.PropertyValueListBottomSheetV2
import com.yunext.virtuals.ui.screen.devicedetail.rememberBottomSheetState

class DeviceDetailSubServiceScreen : Screen {

    @Composable
    override fun Content() {
        DeviceDetailSubServiceScreenImpl(emptyList()) { _, _ -> }
    }
}

@Composable
internal fun DeviceDetailSubServiceScreenImpl(
    source: List<ServiceData>,
    listener: OnServiceListener,
) {
    Debug("[recompose_test_01] DeviceDetailSubServiceScreenImpl size:${source.size} ")
    val list by rememberSaveable(source) {
        mutableStateOf(source)
    }
    Box(Modifier.fillMaxSize()) {
        // HDDebugText("设备详情-属性")
        val coroutineScope = rememberCoroutineScope()
        // 要修改的属性
        var editingService: ServiceData? by remember { mutableStateOf(null) }

        var showTips by remember {
            mutableStateOf("")
        }

        if (list.isEmpty()) {
            TwinsEmptyView()
        } else {
            Box(Modifier.padding(horizontal = 16.dp)) {
                ListTslService(list = list) {
                    editingService = it
                }
            }
        }

        val temp by remember {
            derivedStateOf {
                editingService
            }
        }

        if (temp != null) {
            onEditing(temp, onClose = {
                editingService = null
            }) { service, input ->
                editingService = null
                // 输入
                // fake 输出
                listener.invoke(service, input)
            }
        }


        if (showTips.isNotEmpty()) {
            CHLoadingDialog(showTips) {
                showTips = ""
            }
        }

    }
}

@Composable
private fun BoxScope.onEditing(
    value: ServiceData?,
    onClose: () -> Unit,
    onChanged: (ServiceData, List<PropertyValue<*>>) -> Unit,
) {
    Debug {
        "onEditing:${value.hashCode()}"
    }
    if (value != null) {
        val list by remember(value.input) {
            mutableStateOf(value.input.map {
                it.toDefaultValue()
            })
        }
        // 属性修改
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            val state = rememberBottomSheetState()
            LaunchedEffect(Unit) {
                state.title = "模拟触发服务"
                state.desc = value.desc
                state.msg = value.toString()
            }
            PropertyValueListBottomSheetV2(
                state = state,
                value = list.stable(),
                onClose = onClose,
                onCommitted = {
                    onChanged.invoke(value, it)
                }
            )
        }
    }
}