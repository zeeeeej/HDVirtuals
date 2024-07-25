package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.yunext.kmp.common.logger.XLogType
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.EditTextBlock
import com.yunext.virtuals.ui.common.OnLeft
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsTitle
import com.yunext.virtuals.ui.common.dialog.debugShape
import com.yunext.virtuals.ui.demo.permission.PermissionList
import com.yunext.virtuals.ui.demo.permission.XPermission
import com.yunext.virtuals.ui.demo.permission.anyDenied
import com.yunext.virtuals.ui.demo.permission.rememberPermissionState
import com.yunext.virtuals.ui.theme.Twins
import org.jetbrains.compose.resources.ExperimentalResourceApi
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.slave.BroadcastStatus
import yunext.kotlin.bluetooth.ble.slave.ConnectStatus
import yunext.kotlin.bluetooth.ble.slave.doing
import yunext.kotlin.bluetooth.ble.slave.text

@ExperimentalResourceApi
@Composable
internal fun RTCSlavePage(
    state: RTCSlaveState,
    onLeft: OnLeft,
    onDeviceNameChanged: OnDeviceNameChanged,
    onStartBroadcast: OnStartBroadcast, onStopBroadcast: OnStopBroadcast,
    onUpdateProperty: OnUpdateProperty,
    onConnect: OnConnect,
    debug: Boolean = false,
) {
    var showPermission by remember { mutableStateOf(false) }
    val permissionState =
        rememberPermissionState(
            listOf(
                XPermission.BluetoothConnect,
                XPermission.BluetoothAdvertise
            )
        )
    val rememberScrollState = rememberScrollState()

    TwinsBackgroundBlock(grey = true)



    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState)

    ) {
        TwinsTitle(modifier = Modifier.background(Color.White), text = "Slave", leftClick = {
            onLeft()
        })

        val anyDenied: Boolean by remember {
            derivedStateOf { permissionState.list.anyDenied() }
        }
        if (debug) {
            Text(state.display)
        }

        Text(if (anyDenied) "注意权限！！！" else "权限ok",
            color = if (anyDenied) Color.Red else Color.Black,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { showPermission = !showPermission })

        // 设备
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "设备：",
                modifier = Modifier,
                style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
            )
            AnimatedContent(state.connected) { connected ->
                Text(
                    connected.text, color = when (connected) {
                        is ConnectStatus.Connected -> Color.Green
                        ConnectStatus.Disconnected -> Color.Red
                    }
                )
            }
            Spacer(Modifier.width(10.dp))
            AnimatedContent(state.broadcasting) { broadcasting ->
                Text(
                    broadcasting.text, color = when (broadcasting) {
                        BroadcastStatus.BroadcastStopped -> Color.Red
                        is BroadcastStatus.Broadcasting -> Color.Green
                        is BroadcastStatus.Init -> Color.Blue
                        is BroadcastStatus.Started -> Color.Blue
                    }
                )
            }
        }
        var editing by remember { mutableStateOf(false) }
        var editingAddress by remember { mutableStateOf("") }

        Column(Modifier.padding(start = 16.dp)) {
            AnimatedVisibility(state.accessKey.isNotEmpty()) {
                Text("accessKey:" + state.accessKey, modifier = Modifier, color = Color.LightGray)
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("address:")
                Text(state.address.ifBlank { "请输入设备地址!" }, modifier = Modifier.clickable {
                    editing = true
                })
                Button(onClick = {
                    if (state.broadcasting.doing) {
                        onStopBroadcast()
                    } else {
                        onStartBroadcast()
                    }
                }, enabled = state.address.isNotEmpty()) {

                    Text(if (state.broadcasting.doing) "停止广播" else "开始广播", color = Color.White)
                }
            }
        }


        AnimatedVisibility(editing) {
            TextField(
                value = editingAddress,
                onValueChange = {
                    editingAddress = it

                },
//                keyboardOptions = keyboardOptions,
//                keyboardActions = keyboardActions,
//                modifier = modifier,
                textStyle = Twins.twins_edit_text.copy(textAlign = TextAlign.End),
                placeholder = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "AA:BB:CC:DD:EE:FF",
                        style = Twins.twins_edit_text_hint.copy(textAlign = TextAlign.End)
                    )
                },
                trailingIcon = {
                    Image(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            onDeviceNameChanged(editingAddress)
                            editing = false
                        })
                },
                singleLine = true,
                maxLines = 1,
                shape = RoundedCornerShape(12.dp),
//        colors = TextFieldDefaults.textFieldColors(
//            cursorColor = China.r_luo_xia_hong,
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent
//
//        )

//                colors = TextFieldDefaults.colors().copy(
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    errorIndicatorColor = Color.Transparent,
//
//                    cursorColor = China.r_luo_xia_hong,
//
//
//                    )
            )
        }

        Spacer(Modifier.height(10.dp))

        // 参数
        var showProperty by remember { mutableStateOf(false) }
        Text(
            "属性（参数）：",
            modifier = Modifier.clickable {
                showProperty = !showProperty
            },
            style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
        AnimatedVisibility(showProperty) {
            ParameterView(state.params,state.effect) {
                onUpdateProperty(it)
            }
        }

        Spacer(Modifier.height(10.dp))

        // 日志
        var showLogger by remember { mutableStateOf(true) }
        Text(
            "日志：",
            modifier = Modifier.clickable { showLogger = !showLogger },
            style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
        AnimatedVisibility(showLogger) {
            SlaveLoggerView(state.records)
        }

        Spacer(Modifier.height(10.dp))

        // 其他
        var showOther by remember { mutableStateOf(false) }
        Text(
            "其他：",
            modifier = Modifier.clickable { showOther = !showOther },
            style = TextStyle.Default.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
        AnimatedVisibility(showOther) {
            Row {
                Button(onClick = onStartBroadcast) {
                    Text("开始广播", color = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Button(onClick = onStopBroadcast) {
                    Text("停止广播", color = Color.White)
                }
            }
        }

    }

    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(showPermission, modifier = Modifier.align(Alignment.BottomCenter)) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(16.dp),
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
private fun SlaveLoggerView(records: List<XBleRecord>) {
    val rememberLazyListState = rememberLazyListState()
    LaunchedEffect(records.size) {
        rememberLazyListState.scrollToItem(records.size)
    }
    Box(Modifier.fillMaxWidth().height(400.dp).hdBackground { Color.LightGray }) {
        LazyColumn(Modifier.fillMaxWidth(), rememberLazyListState) {
            items(records, { it.toString() }) {
                RecordItem(it)
            }
        }
    }
}

@Composable
private fun RecordItem(record: XBleRecord) {
    Text(
        record.toString(), color = when (record.type) {
            XLogType.D -> Color.Black
            XLogType.I -> Color.Blue
            XLogType.W -> Color.Cyan
            XLogType.E -> Color.Red
        }, fontSize = 11.sp, fontWeight = FontWeight.Light
    )
}

