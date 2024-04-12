package com.yunext.virtuals.ui.screen.devicedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yunext.virtuals.ui.screen.devicedetail.property.ListTslProperty
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DeviceDetailSubPropertyScreen(val device: DeviceAndStateViewData) : Screen {


    @Composable
    override fun Content() {
        DeviceDetailSubPropertyScreenImpl(emptyList()){}
    }
}

@Composable
internal fun DeviceDetailSubPropertyScreenImpl(
    list: List<PropertyData>,
    onPropertyChanged: (PropertyData) -> Unit,
) {
    Debug("[recompose_test_01] DeviceDetailSubPropertyScreenImpl size:${list.size} ")
    Box(Modifier.fillMaxSize()) {
        // HDDebugText("设备详情-属性")
        val coroutineScope = rememberCoroutineScope()
        // 要修改的属性
        var editingProperty: PropertyData? by remember { mutableStateOf(null) }
        // 是否添加属性
        var addProperty by remember { mutableStateOf(false) }
        //
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

        // 属性修改
        val temp = editingProperty
        if (temp != null) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                PropertyBottomSheet(temp, onClose = {
                    editingProperty = null
                }, onCommitted = {
                    editingProperty = null
                    Napier.v("修改结果：${it.value.real.displayValue}")
                    onPropertyChanged.invoke(it)
//                    coroutineScope.launch {
//                        showTips = "选择了$it"
//                        Napier.v("修改结果：${it.value.displayValue}")
//                        delay(3000)
//                        showTips = ""
//                    }
                }, onAdd = false to {
                    addProperty = true
                })
            }
        }

        // 属性添加
        if (addProperty) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {

                    PropertyBottomSheet(editingProperty!!/* TODO */, onClose = {
                        addProperty = false
                    }, onCommitted = {
                        addProperty = false
                        coroutineScope.launch {
                            showTips = "添加了$it"
                            delay(3000)
                            showTips = ""
                        }

                    }, onAdd = true to {

                    }
                    )
                }
            }
        }

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