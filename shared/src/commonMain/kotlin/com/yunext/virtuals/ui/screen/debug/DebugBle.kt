package com.yunext.virtuals.ui.screen.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.context.Activity
import com.yunext.kmp.context.hdContext
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.kmp.ui.compose.hdBorder
import com.yunext.virtuals.ui.demo.permission.PermissionList
import com.yunext.virtuals.ui.demo.permission.XPermission
import com.yunext.virtuals.ui.demo.permission.rememberPermissionState
import com.yunext.virtuals.ui.demo.permission.requestPermission
import com.yunext.virtuals.ui.screen.rtctest.BleDeviceVo
import com.yunext.virtuals.ui.screen.rtctest.RTCMasterState
import kotlinx.coroutines.launch

@Composable
internal fun BoxScope.DebugBle(toBle: () -> Unit) {
    Button(
        onClick = toBle,
        modifier = Modifier.Companion
            .align(Alignment.BottomStart)
            .padding(vertical = 16.dp)
    ) {
        Text(text = "跳转到蓝牙")
    }
}

@Composable
fun DebugBluetoothLe(
    state: RTCMasterState,
    onStartScan: () -> Unit = {},
    onStopScan: () -> Unit = {},
    onConnect: (BleDeviceVo) -> Unit,
    onEnableNotify: (BleDeviceVo) -> Unit,
) {
    Spacer(modifier = Modifier.height(0.dp))
    var sel: BleDeviceVo? by remember { mutableStateOf(null) }

    val permissionState =
        rememberPermissionState(listOf(XPermission.BluetoothConnect, XPermission.BluetoothScan))
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PermissionList(Modifier, permissionState.list) { p, s ->
            permissionState.update(p, s)
        }
    }

    var scanning: Boolean by remember { mutableStateOf(state.scanning) }
    val scanBtn by remember {
        derivedStateOf {
            if (scanning) "停止搜索" else "开始搜索"
        }
    }

    val scanList by remember(state.scanningDeviceList) { mutableStateOf(state.scanningDeviceList) }
    Row {
        Button(onClick = {
            scanning = !scanning
            if (scanning) {
                onStartScan()
            } else {
                onStopScan()
            }
        }) {
            Text(scanBtn)
        }
        Button(onClick = {
            val cur = sel
            if (cur != null) {
                onEnableNotify(cur)
            }
        }) {
            Text("enable notify")
        }
    }


    Text("$sel", color = Color.Green)
    ScanList(scanList) {
        sel = it
        onConnect(it)
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
fun ScanItem(bleDevice: BleDeviceVo, onSelected: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.hdBackground().padding(12.dp).clickable {
            onSelected()
        }
    ) {
        Box(modifier = Modifier.width(44.dp).aspectRatio(1f).hdBorder(debug = true)) {
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