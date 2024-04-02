package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.kmp.mqtt.interop.DebugInterOp
import com.yunext.kmp.mqtt.interop.debugSendByteArrayWrapper
import com.yunext.kmp.mqtt.virtuals.test.MQTTVirtualsDemo
import com.yunext.virtuals.ui.common.dialog.CHAlertDialog
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.processing
import com.yunext.virtuals.ui.screen.adddevice.AddDeviceScreen
import com.yunext.virtuals.ui.screen.devicedetail.DeviceDetailScreen
import io.ktor.utils.io.core.toByteArray

private val testHDMQTT2: MQTTVirtualsDemo by lazy { MQTTVirtualsDemo() }

class DeviceListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<DeviceListScreenModel> {
            DeviceListScreenModel()
        }
        val state by screenModel.state.collectAsState()
        var debug by remember { mutableStateOf(false) }
        var confirmDeleteDevice: DeviceAndStateViewData? by remember { mutableStateOf(null) }
        var confirmDisconnectDevice: DeviceAndStateViewData? by remember { mutableStateOf(null) }


        LaunchedEffect(Unit) {
            screenModel.doGetAllDevice()
        }

        //Debug(state.list.joinToString ("\n"){ it.toString() })

        Box(Modifier.fillMaxSize()) {
            DeviceListScreenImpl(
                modifier = Modifier.fillMaxSize(),
                list = state.list,
                onRefresh = {
                    screenModel.doGetAllDevice()
                },
                onDeviceSelected = {
                    navigator.push(DeviceDetailScreen(it))
                },
                onActionAdd = {
                    navigator.push(AddDeviceScreen())
                },
                onDeviceDelete = {
                    confirmDeleteDevice = it
                },
                onDisconnect = {
                    confirmDisconnectDevice = it
                }
            )
        }


        if (confirmDeleteDevice != null) {
            val d = confirmDeleteDevice
            ConfirmDeleteDeviceDialog(
                d,
                cancel = { confirmDeleteDevice = null },
                screenModel::doDeleteDevice
            )
        }

        if (confirmDisconnectDevice != null) {
            val d = confirmDisconnectDevice
            ConfirmDisconnectDeviceDialog(
                d,
                cancel = { confirmDisconnectDevice = null },
                screenModel::doDisconnectDevice
            )
        }

        if (state.effect.processing) {
            CHLoadingDialog("Loading", dimAmount = .5f) {

            }
        }

        if (debug) {
            DebugBlock()
        }

    }


}

@Composable
private fun ConfirmDeleteDeviceDialog(
    deviceAndStateViewData: DeviceAndStateViewData?,
    cancel: () -> Unit,
    confirm: (DeviceAndStateViewData) -> Unit,
) {
    if (deviceAndStateViewData != null) {
        CHAlertDialog(msg = "删除设备${deviceAndStateViewData.name}/${deviceAndStateViewData.model}?",
            onRight = {
                confirm.invoke(deviceAndStateViewData)
            }
        ) {
            cancel()
        }
    }
}

@Composable
private fun ConfirmDisconnectDeviceDialog(
    deviceAndStateViewData: DeviceAndStateViewData?,
    cancel: () -> Unit,
    confirm: (DeviceAndStateViewData) -> Unit,
) {
    if (deviceAndStateViewData != null) {
        CHAlertDialog(msg = "断开设备连接${deviceAndStateViewData.name}/${deviceAndStateViewData.model}?",
            onRight = {
                confirm.invoke(deviceAndStateViewData)
            }
        ) {
            cancel()
        }
    }
}

@Composable
private fun DebugBlock() {
    Column(modifier = Modifier.padding(start = 100.dp, top = 100.dp)) {


        Button(onClick = {
            testHDMQTT2.init()
        }, modifier = Modifier) {
            Text("KMQTT 连接")

        }

        Button(onClick = {
            testHDMQTT2.register()
        }, modifier = Modifier) {
            Text("KMQTT 注册")

        }

        Button(onClick = {
            testHDMQTT2.publish()
        }, modifier = Modifier) {
            Text("KMQTT 发消息")

        }

        Button(onClick = {
            testHDMQTT2.disconnect()
        }, modifier = Modifier) {
            Text("KMQTT 关闭")
        }

        Button(onClick = {
            println("kotlin 传递 byteArray 到 swift 开始")
            val data = "hello ydd".toByteArray()
            DebugInterOp.interOp_Out_ByteArray?.invoke(data)
            println("kotlin 传递 byteArray 到 swift 结束")
        }, modifier = Modifier) {
            Text("kotlin 传递 byteArray 到 swift")
        }

        Button(onClick = {
            println("kotlin 传递 byteArray 到 swift 开始 2")
            val data = "hello ydd".toByteArray()
            debugSendByteArrayWrapper(data)
            println("kotlin 传递 byteArray 到 swift 结束 2")
        }, modifier = Modifier) {
            Text("kotlin 传递 byteArray 到 swift 2")
        }

    }
}