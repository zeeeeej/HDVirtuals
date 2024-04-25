package com.yunext.virtuals.ui.screen.devicedetail.screenvoyager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.resource.color.app_background_70
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsTitle
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.MenuData
import com.yunext.virtuals.ui.data.iconRes
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.DeviceDetailTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.MenuList
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.Top
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
@Composable
@Deprecated("见DeviceDetailScreenImplNew", ReplaceWith("DeviceDetailScreenImplNew"))
internal fun VoyagerDeviceDetailScreenImpl(
    device: DeviceAndStateViewData,
    onMenuClick: (MenuData) -> Unit,
    onPropertyEdit: (PropertyValue<*>) -> Unit,
    onLeft: () -> Unit,
) {
    // 背景
    TwinsBackgroundBlock()

    // for test
    val msg = remember {
        mutableStateOf("hello kmp! size=${device.propertyList.size}")
    }

    // 设置menu
    var showMenu by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1 标题
        TwinsTitle(modifier = Modifier
            .hdBackground { app_background_70 },
            text = device.name,
            icon = device.status.iconRes,
            leftClick = {
                onLeft()
            },
            rightClick = {
                showMenu = !showMenu
            })

        // 2 内容
        Box(Modifier.fillMaxWidth()) {
            Column() {
                // 2.1 top
                //region 设备基本信息
                Column(
                    Modifier
                        .fillMaxWidth()
                        .hdBackground { app_background_70 }
                        .padding(vertical = 0.dp, horizontal = 12.dp),
                ) {
                    Top(device = device)
                    Spacer(Modifier.height(12.dp))
                }
                //endregion


                // 2.2
                //region 设备tab + tab内容
                var curTab: Int by remember {
                    mutableStateOf(0)
                }

                val propertyList = remember() {
                    mutableStateOf(device.propertyList)
                }

                val eventList = remember() {
                    mutableStateOf(device.eventList)
                }

                val serviceList = remember() {
                    mutableStateOf(device.serviceList)
                }

                LaunchedEffect(device) {
                    propertyList.value = device.propertyList
                }

                val list: List<HDDeviceTabForVoyager> by remember {
                    mutableStateOf(
//                        listOf(
//                            PropertiesTabVoyager(propertyList, msg, { msg.value = it }) {
//
//                            },
//                            EventsTabVoyager(eventList, msg, { msg.value = it }) {
//
//                            },
//                            ServicesTabVoyager(serviceList, msg, { msg.value = it }) {
//
//                            }
//                        )
                        listOf(
                            PropertiesTabVoyager({ msg.value = it }) {
                                curTab = 1
                            },
                            EventsTabVoyager(object :MyOnMsgChanged{
                                override fun onChanged(m: String) {
                                    msg.value  = m
                                }

                            }),
                            ServicesTabVoyager({ msg.value = it }) {
                                curTab = 0
                            }
                        )

                    )
                }
                Text("${device.propertyList.size}/${device.eventList.size}/${device.serviceList.size}/$curTab/${msg} ")
                // 2.2.1 tab
                DeviceDetailTabsForVoyager(list[curTab], list) {
                    curTab = when (it) {
                        is PropertiesTabVoyager -> 0
                        is EventsTabVoyager -> 1
                        is ServicesTabVoyager -> 2
                    }
                }
                // 2.2.2 content
                Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.Yellow)) {

                    TabNavigator(
                        tab = list[curTab],
                        disposeNestedNavigators = false,
                        tabDisposable = {
                            TabDisposable(
                                navigator = it,
                                tabs = list
                            )
                        },
                    ) { tabNavi ->
                        LaunchedEffect(Unit) {
                            snapshotFlow {
                                curTab
                            }.collect {
                                tabNavi.current = list[it]
                            }
                        }
                        CurrentTab()
                    }
                }
                //endregion
            }

            // pop
            if (showMenu) {
                MenuList(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .align(Alignment.TopEnd), onDismiss = { showMenu = false }) {
                    onMenuClick(it)
                }
            }
        }
    }
}

@Composable
private fun DeviceDetailTabsForVoyager(
    selected: HDDeviceTabForVoyager,
    tabs: List<HDDeviceTabForVoyager>,
    onClick: (HDDeviceTabForVoyager) -> Unit,
) {
    Row(Modifier.fillMaxWidth()) {
        tabs.forEach { tab ->
            key(tab.key) {
                DeviceDetailTab(tab.options.title, selected = tab == selected) {
                    onClick(tab)
                }
            }
        }
    }
}