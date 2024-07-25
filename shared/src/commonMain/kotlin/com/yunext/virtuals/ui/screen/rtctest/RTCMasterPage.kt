package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.ui.compose.hdBorder
import com.yunext.virtuals.ui.common.OnLeft
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsTitle
import com.yunext.virtuals.ui.common.dialog.debugShape
import com.yunext.virtuals.ui.demo.permission.PermissionList
import com.yunext.virtuals.ui.demo.permission.XPermission
import com.yunext.virtuals.ui.demo.permission.rememberPermissionState
import com.yunext.virtuals.ui.theme.Twins
import org.jetbrains.compose.resources.ExperimentalResourceApi
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.display
import yunext.kotlin.bluetooth.ble.master.BleMasterConnectedStatus
import yunext.kotlin.bluetooth.ble.master.BleMasterScanningStatus
import yunext.kotlin.bluetooth.ble.master.connected

@ExperimentalResourceApi
@Composable
internal fun RTCMasterPage(
    state: RTCMasterState,
    onLeft: OnLeft,
    onStartScan: OnMasterStartScan, onStopScan: OnMasterStopScan,
    onConnect: OnMasterConnect,
    onDisconnect: OnMasterDisconnect,
    onEnableNotify: OnMasterEnableNotify,
    onWrite: OnMasterWrite,
    onRead: OnMasterRead,
    onAuth: OnAuth,
    onSetProperty: OnSetProperty,
    onSetTimestamp: OnSetTimestamp,
    onTestAuthed: OnTestAuthed,
    onTestUnAuthed: OnTestUnAuthed
) {
    @OptIn(ExperimentalResourceApi::class) (TwinsBackgroundBlock(grey = true))

    Column(
        modifier = Modifier.fillMaxSize()

    ) {
        TwinsTitle(modifier = Modifier.background(Color.White), text = "Master", leftClick = {
            onLeft()
        })
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Column {
                DebugBluetoothLe(
                    state,
                    onStartScan = onStartScan,
                    onStopScan = onStopScan,
                    onConnect = onConnect,
                    onDisconnect = onDisconnect,
                    onEnableNotify = onEnableNotify,
                    onWrite = onWrite,
                    onRead = onRead,
                    onAuth = onAuth,
                    onSetProperty = onSetProperty,
                    onSetTimestamp = onSetTimestamp,
                    onTestAuthed = onTestAuthed,
                    onTestUnAuthed = onTestUnAuthed
                )
            }

            Box(Modifier.align(Alignment.BottomCenter)) { PermissionView() }
        }

    }
}

@Composable
private fun PermissionView() {
    var showPermission by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { showPermission = !showPermission }) {
            Text(if (showPermission) "hide" else "show")
        }
        AnimatedVisibility(showPermission) {
            val permissionState =
                rememberPermissionState(
                    listOf(
                        XPermission.BluetoothConnect,
                        XPermission.BluetoothScan
                    )
                )
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PermissionList(Modifier, permissionState.list) { p, s ->
                    permissionState.update(p, s)
                }
            }
        }
    }


}


@Composable
internal fun BoxScope.DebugBle(toBle: () -> Unit) {
    Button(
        onClick = toBle,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(vertical = 16.dp)
    ) {
        Text(text = "跳转到蓝牙")
    }
}

@Composable
private fun DebugBluetoothLe(
    state: RTCMasterState,
    onStartScan: OnMasterStartScan, onStopScan: OnMasterStopScan,
    onConnect: OnMasterConnect,
    onDisconnect: OnMasterDisconnect,
    onEnableNotify: OnMasterEnableNotify,
    onWrite: OnMasterWrite,
    onRead: OnMasterRead,
    onAuth: OnAuth,
    onSetProperty: OnSetProperty,
    onSetTimestamp: OnSetTimestamp,
    onTestAuthed: OnTestAuthed,
    onTestUnAuthed: OnTestUnAuthed
) {
    Spacer(modifier = Modifier.height(0.dp))
    val scanBtn by remember(state.scanning) {
        derivedStateOf {
            when (state.scanning) {
                BleMasterScanningStatus.ScanStopped -> "开始搜索"
                is BleMasterScanningStatus.Scanning -> "停止搜索"
            }
        }
    }
    val scanList by remember(state.scanningDeviceList) { mutableStateOf(state.scanningDeviceList) }

    androidx.compose.material3.Text(
        "设备：",
        modifier = Modifier,
        style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            when (state.connecting) {
                is BleMasterConnectedStatus.Connected -> "${state.connecting.device.display}-已连接"
                is BleMasterConnectedStatus.Connecting -> "${state.connecting.device.display}-连接中"
                is BleMasterConnectedStatus.Disconnected -> "${state.connecting.device.display}-已断开"
                is BleMasterConnectedStatus.Disconnecting -> "${state.connecting.device.display}-断开中"
                BleMasterConnectedStatus.Idle -> "无"
            }
        )
        Spacer(Modifier.width(12.dp))
//        Text("未连接", modifier = Modifier.clickable {
//            //
//        })
    }

    AnimatedVisibility(
        state.connecting.connected && state.services.isNotEmpty(),
        Modifier.fillMaxWidth()
    ) {
        // Text("${state.services.size}个")
        ServiceListView(state.services)
    }

    androidx.compose.material3.Text(
        "搜索：",
        modifier = Modifier,
        style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
    )
    //<editor-fold desc="scan">
    Row {
        Button(onClick = {
            when (state.scanning) {
                BleMasterScanningStatus.ScanStopped -> onStartScan()
                is BleMasterScanningStatus.Scanning -> onStopScan()
            }
        }) {
            Text(scanBtn)
        }

    }
    ScanList(scanList) {
        onConnect(it)
    }
    //</editor-fold>
    androidx.compose.material3.Text(
        "基础操作：",
        modifier = Modifier,
        style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
    )
    Row {
        Button(onClick = {
            onEnableNotify()
        }, enabled = state.connecting.connected) {
            Text("enable notify")
        }
        Spacer(Modifier.width(12.dp))
        Button(onClick = {
            onWrite()
        }, enabled = state.connecting.connected) {
            Text("write")
        }
        Spacer(Modifier.width(12.dp))
        Button(onClick = {
            onRead()
        }, enabled = state.connecting.connected) {
            Text("read")
        }

    }

    androidx.compose.material3.Text(
        "业务操作：",
        modifier = Modifier,
        style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
    )
    var showAuth by remember {
        mutableStateOf(false)
    }
    var showProperty by remember {
        mutableStateOf(false)
    }
    Row {
        Button(onClick = {
            showAuth = !showAuth
        }, enabled = state.connecting.connected) {
            Text("auth")
        }
        Spacer(Modifier.width(12.dp))
        Button(onClick = {
            showProperty=!showProperty
        }, enabled = true||state.connecting.connected) {
            Text("setProperty")
        }
        Spacer(Modifier.width(12.dp))
        Button(onClick = {
            onSetTimestamp()
        }, enabled = state.connecting.connected) {
            Text("setTimestamp")
        }

    }

    Row {
        Button(onClick = {
            onTestAuthed()
        }, enabled = state.connecting.connected) {
            Text("test auth")
        }
        Spacer(Modifier.width(12.dp))
        Button(onClick = {
            onTestUnAuthed()
        }, enabled = state.connecting.connected) {
            Text("test un authed")
        }


    }


    AnimatedVisibility(showAuth, Modifier.fillMaxWidth()) {
        Column {
            AuthView("鉴权日志：${
                when (val effect = state.authEffect) {
                    is AuthEffect.Fail -> "失败${effect.msg}"
                    AuthEffect.Idle -> "等待鉴权"
                    is AuthEffect.Start -> "鉴权中..."
                    is AuthEffect.Success -> "鉴权成功！"
                }
            }", onAuth = onAuth, onClose = { showAuth = !showAuth })
        }
    }

    AnimatedVisibility(showProperty) {
        ParameterView(state.params,state.setPropertyEffect) {
            onSetProperty(it.key,it.value)
        }
    }


}

@Composable
private fun AuthView(authMsg: String, onAuth: OnAuth, onClose: () -> Unit) {
    var auth: String by remember {
        mutableStateOf("")
    }

    //region edit text
    TextField(
        value = auth,
        onValueChange = {
            auth = it

        },
        textStyle = Twins.twins_edit_text.copy(textAlign = TextAlign.Start),
        placeholder = {
            androidx.compose.material3.Text(
                modifier = Modifier.fillMaxWidth(),
                text = "hadlinks_rtc",
                style = Twins.twins_edit_text_hint.copy(textAlign = TextAlign.End)
            )
        },
        label = {
            Text("accessKey为鉴权密钥，切勿对外公布。", color = Color.Red, fontSize = 11.sp)
        },
        leadingIcon = {
            Image(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onClose()
                })
        },
        trailingIcon = {
            Image(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onAuth(auth)
                })
        },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(12.dp),
    )

    //endregion
    Text(authMsg)

}

@Composable
private fun ServiceListView(services: List<XBleService>) {
    LazyColumn(
        Modifier
//        .wrapContentSize(unbounded = true)
            .fillMaxWidth().debugShape(true).padding(16.dp)
    ) {
        items(services, { it.uuid }) {
            ServiceItem(it)
        }
    }
}

@Composable
private fun ServiceItem(service: XBleService) {
    Column {
        Text(service.uuid)
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            service.characteristics.forEach {
                key(it.uuid) {
                    CharacteristicsItem(it)
                }
            }
        }

//        LazyColumn(Modifier
////            .wrapContentSize(unbounded = true)
//            .fillMaxWidth().debugShape(true).padding(horizontal = 16.dp)) {
//            items(service.characteristics,{it.uuid}){
//                CharacteristicsItem(it)
//            }
//        }
    }
}

@Composable
private fun CharacteristicsItem(characteristics: XBleCharacteristics) {
    Row {
        Text(characteristics.uuid)
        Text(characteristics.properties.joinToString(",") {
            "${it.name}"
        })
    }
}


@Composable
private fun ScanList(list: List<BleDeviceVo>, onSelected: (BleDeviceVo) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(list, { it.mac }) {
            ScanItem(it) {
                onSelected(it)
            }
        }
    }
}

@Composable
private fun ScanItem(bleDevice: BleDeviceVo, onSelected: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .debugShape(true)
//            .hdBackground { Color.LightGray }
            .padding(12.dp).clickable {
                onSelected()
            }
    ) {
        Box(
            modifier = Modifier.width(44.dp).aspectRatio(1f)
                .hdBorder(debug = false)
        ) {
            Text(
                bleDevice.rssi.toString(),
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.width(12.dp))
        Column(
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            horizontalAlignment = Alignment.Start,
        ) {
            Text(bleDevice.name)
            Text(bleDevice.mac, fontWeight = FontWeight.Bold)
        }
    }
}

//@Composable
//internal fun BluetoothPermissionStatus(modifier: Modifier, permission: HDPermission) {
//    val defaultStatus: HDPermissionStatus = checkPermission(permission)
//    var status: HDPermissionStatus by remember { mutableStateOf(defaultStatus) }
//    var trigger: Long? by remember { mutableStateOf(null) }
//    Box(modifier
//        .hdBackground()
//        .clickable() {
//        trigger = currentTime()
//    }) {
//        requestPermission(permission, key = trigger) {
//            status = if (it) HDPermissionStatus.Granted else HDPermissionStatus.DENIED
//            HDLogger.w("requestPermission", "statue:$status")
//        }
//        Text(
//            permission.name, color = when (status) {
//                HDPermissionStatus.Granted -> Color.Black
//                HDPermissionStatus.DENIED -> Color.Red
//            }, modifier = Modifier.align(Alignment.Center)
//        )
//    }
//}
//
//
//
////@Composable
////expect fun checkPermission(permission: HDPermission): HDPermissionStatus
//
//@Composable
//expect fun requestPermission(permission: HDPermission, key: Any?, onHDPermission: (Boolean) -> Unit)