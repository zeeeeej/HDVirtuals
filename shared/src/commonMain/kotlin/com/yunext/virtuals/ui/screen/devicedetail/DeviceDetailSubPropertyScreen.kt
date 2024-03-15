package com.yunext.virtuals.ui.screen.devicedetail

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.TwinsEmptyView
import com.yunext.virtuals.ui.common.dialog.CHAlertDialog
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeviceDetailSubPropertyScreen : Screen {


    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize()) {
            // HDDebugText("设备详情-属性")
            val coroutineScope = rememberCoroutineScope()
            val list by remember {
                mutableStateOf(List(20) { it })
            }
            var editingProperty: Any? by remember { mutableStateOf(null) }
            var addProperty by remember { mutableStateOf(false) }
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
                        coroutineScope.launch {
                            showTips = "选择了$it"
                            delay(3000)
                            showTips = ""
                        }
                    }, add = false to {
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
                        PropertyBottomSheet("", onClose = {
                            addProperty = false
                        }, onCommitted = {
                            addProperty = false
                            coroutineScope.launch {
                                showTips = "添加了$it"
                                delay(3000)
                                showTips = ""
                            }

                        }, add = true to {

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
        }
    }
}