package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.virtuals.ui.common.dialog.CHAlertDialog
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.processing
import com.yunext.virtuals.ui.screen.adddevice.AddDeviceScreen
import com.yunext.virtuals.ui.screen.configwifi.ConfigWiFiScreen
import com.yunext.virtuals.ui.screen.rtctest.RTCSelectedDialog
import com.yunext.virtuals.ui.screen.debug.DebugBle
import com.yunext.virtuals.ui.screen.debug.DebugMqttBlock
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.DeviceDetailScreen
import com.yunext.virtuals.ui.screen.devicedetail.screenvoyager.VoyagerDeviceDetailScreen
import com.yunext.virtuals.ui.screen.rtctest.RTCTestScreen


private const val UseVoyagerTab = false

class DeviceListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<DeviceListScreenModel> {
            DeviceListScreenModel()
        }
        val state by screenModel.state.collectAsState()
        var debugMqtt by remember { mutableStateOf(false) }
        var confirmDeleteDevice: DeviceAndStateViewData? by remember { mutableStateOf(null) }
        var confirmDisconnectDevice: DeviceAndStateViewData? by remember { mutableStateOf(null) }
        var dialogForRTC: Boolean by remember {
            mutableStateOf(false)
        }

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
                    val detailScreen =
                        if (UseVoyagerTab) VoyagerDeviceDetailScreen(it) else DeviceDetailScreen(it)
                    navigator.push(detailScreen)
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

            DebugBle {
//                navigator.push(ConfigWiFiScreen())
                dialogForRTC = true
            }
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

        if (debugMqtt) {
            DebugMqttBlock()
        }


        if (dialogForRTC) {
            RTCSelectedDialog() {
                navigator.push(RTCTestScreen(it))
                dialogForRTC = false
            }
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
