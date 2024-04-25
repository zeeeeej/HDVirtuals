package com.yunext.virtuals.ui.screen.devicedetail.deviceproperty

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.TwinsEmptyView
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.PropertyData
import com.yunext.virtuals.ui.screen.devicedetail.bottomsheet.PropertyBottomSheet
import io.github.aakira.napier.Napier

internal class DeviceDetailSubPropertyScreen(val device: DeviceAndStateViewData) : Screen {


    @Composable
    override fun Content() {
        DeviceDetailSubPropertyScreenImpl(emptyList()) {}
    }
}

@Composable
internal fun DeviceDetailSubPropertyScreenImpl(
    source: List<PropertyData>,
    onPropertyChanged: (PropertyData) -> Unit,
) {
    Debug("[recompose_test_01] DeviceDetailSubPropertyScreenImpl size:${source.size} ")
    val list by rememberSaveable(source) {
        mutableStateOf(source)
    }
    Box(Modifier.fillMaxSize()) {
        // HDDebugText("设备详情-属性")
        val coroutineScope = rememberCoroutineScope()
        // 要修改的属性
        var editingProperty: PropertyData? by remember { mutableStateOf(null) }

        var showTips by remember {
            mutableStateOf("")
        }

        if (list.isEmpty()) {
            TwinsEmptyView()
        } else {
            Box(Modifier.padding(horizontal = 16.dp)) {
                ListTslProperty(list = list) {
                    if (editingProperty == null) {
                        editingProperty = it
                    }
                }
            }
        }

        onEditing(editingProperty, onClose = {
            editingProperty = null
        }, onPropertyChanged)

        if (showTips.isNotEmpty()) {
            CHLoadingDialog(showTips) {
                showTips = ""
            }
        }

//            val screenModel = getScreenModel<DeviceDetailScreenModel>()
//            val screenModel = rememberScreenModel<DeviceDetailScreenModel>(){
//                DeviceDetailScreenModel()
//            }
        LaunchedEffect(Unit) {
//                Napier.d("aaa:${navigator.hashCode()}")
//                Napier.d("bbb:${navigator2.hashCode()}")
//                Napier.d("screenModel:${screenModel.hashCode()}")
        }

    }
}

@Composable
private fun BoxScope.onEditing(
    editingProperty: PropertyData?,
    onClose: () -> Unit,
    onPropertyChanged: (PropertyData) -> Unit,
) {
    Debug {
        "onEditing:${editingProperty.hashCode()}"
    }
    // 属性修改
    if (editingProperty != null) {
        Box(modifier = Modifier.Companion.align(Alignment.BottomCenter)) {
            PropertyBottomSheet(editingProperty, onClose = {
                onClose()
            }, onCommitted = {
                onClose()
                Napier.v("修改结果：${it.value.value.displayValue}")
                onPropertyChanged.invoke(it)
//                    coroutineScope.launch {
//                        showTips = "选择了$it"
//                        Napier.v("修改结果：${it.value.displayValue}")
//                        delay(3000)
//                        showTips = ""
//                    }
            })
        }
    }
}