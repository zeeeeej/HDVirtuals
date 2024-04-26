package com.yunext.virtuals.ui.screen.devicedetail.screennormal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_background_70
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.HDImage
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsTitle
import com.yunext.virtuals.ui.common.dialog.XPopContainer
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.MenuData
import com.yunext.virtuals.ui.data.iconRes
import com.yunext.virtuals.ui.screen.devicedetail.SelectedHDDeviceTab
import com.yunext.virtuals.ui.screen.devicedetail.SelectedHDDeviceTabV2
import com.yunext.virtuals.ui.screen.devicedetail.deviceevent.DeviceDetailSubEventScreen
import com.yunext.virtuals.ui.screen.devicedetail.deviceevent.DeviceDetailSubEventScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceproperty.DeviceDetailSubPropertyScreen
import com.yunext.virtuals.ui.screen.devicedetail.deviceproperty.DeviceDetailSubPropertyScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.DeviceDetailSubServiceScreen
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.DeviceDetailSubServiceScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnEventListener
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnPropertyListener
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnServiceListener
import com.yunext.virtuals.ui.screen.devicedetail.screenviewpager.DeviceDetailContentWithViewPager
import com.yunext.virtuals.ui.screen.devicelist.DeviceCommunicationIdAndModel
import com.yunext.virtuals.ui.theme.ItemDefaults
import org.jetbrains.compose.resources.ExperimentalResourceApi

private const val VP = true

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun DeviceDetailScreenImplNew(
    device: DeviceAndStateViewData,
    onMenuClick: (MenuData) -> Unit,
    onPropertyEdit: OnPropertyListener,
    onEventTrigger: OnEventListener,
    onServiceListener: OnServiceListener,
    onLeft: () -> Unit,
) {
    TwinsBackgroundBlock()
    var showMenu by remember { mutableStateOf(false) }
//    val propertyListSize by remember {
//        derivedStateOf{
//            device.propertyList.size
//        }
//    }


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
            Column {
                // 2.1 top
                Column(
                    Modifier
                        .fillMaxWidth()
                        .hdBackground { app_background_70 }
                        .padding(vertical = 0.dp, horizontal = 12.dp),
                ) {
                    Top(device = device)
                    Spacer(Modifier.height(12.dp))
                }

                // 2.2 tab+content
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {

                    if (VP) {
                        // 使用vp的实现
                        DeviceDetailContentWithViewPager(
                            propertyList = device.propertyList,
                            eventList = device.eventList,
                            serviceList = device.serviceList,
                            onPropertyListener = onPropertyEdit,
                            onEventListener = onEventTrigger,
                            onServiceListener = onServiceListener
                        )
                    } else {
                        // warn：一开始的实现，数据没有缓存，切换tab会重新刷新tab里的页面
                        Column {
                            var curTab: HDDeviceTab by remember { mutableStateOf(PropertiesTab) }
                            DeviceDetailTabs(curTab, deviceDetailTabs) {
                                curTab = it
                            }
                            when (curTab) {
                                PropertiesTab -> DeviceDetailSubPropertyScreenImpl(device.propertyList) {
                                    onPropertyEdit.invoke(it.value.value)
                                }

                                EventsTab -> DeviceDetailSubEventScreenImpl(device.eventList) { _, _ ->
                                }

                                ServicesTab -> DeviceDetailSubServiceScreenImpl(
                                    device.serviceList,
                                    onServiceListener
                                )
                            }

                            // 暂且不用 voyager tab 实现
                            //Navigator(DeviceDetailTabsScreen(curTab,device))
                        }
                    }
                }
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


@Deprecated("暂且不用 voyager tab 实现")
private class DeviceDetailTabsScreen(
    private val tab: HDDeviceTab,
    private val device: DeviceAndStateViewData,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(tab) {
            val innerScreen = when (tab) {
                EventsTab -> DeviceDetailSubPropertyScreen(device)
                PropertiesTab -> DeviceDetailSubEventScreen()
                ServicesTab -> DeviceDetailSubServiceScreen()
            }
            navigator.replace(innerScreen)
        }
    }
}

@Composable
internal fun DeviceDetailTabsV3(
    modifier: Modifier,
    curTab: HDDeviceTab,
    tabs: List<HDDeviceTab>,
    onClick: (HDDeviceTab) -> Unit,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = curTab.index,
        backgroundColor = Color.Transparent,
        divider = {
            TabRowDefaults.Divider(
                color = Color.Transparent
            )
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                color = app_appColor,
                height = TabRowDefaults.IndicatorHeight * 2,
                modifier = Modifier.tabIndicatorOffset(tabPositions[curTab.index])
            )
        },

        tabs = {
            HDSelectedTabs(tabs, curTab, onClick)
        }
    )
}

@Composable
private fun HDSelectedTabs(
    tabs: List<HDDeviceTab>,
    curTab: HDDeviceTab,
    onClick: (HDDeviceTab) -> Unit,
) {
    tabs.forEach { tab ->
        key(tab.key) {
            Tab(text = {
                SelectedHDDeviceTabV2(
                    Modifier,
                    tab = tab.options.title, selected = curTab == tab
                )
            }, selected = curTab == tab, onClick = {
                onClick.invoke(tab)
            })

        }
    }
}

@Deprecated("for test")
@Composable
internal fun DeviceDetailTabsV4(
    modifier: Modifier,
    curTab: HDDeviceTab,
    tabs: List<HDDeviceTab>,
    onClick: (HDDeviceTab) -> Unit,
) {
    ScrollableTabRow(
        selectedTabIndex = curTab.index,
        modifier = modifier,
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        divider = {
            TabRowDefaults.Divider(
                color = Color.Transparent
            )
        },
        indicator = { tabPositions ->
            androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                color = app_appColor,
                height = TabRowDefaults.IndicatorHeight * 2,
                modifier = Modifier.tabIndicatorOffset(tabPositions[curTab.index])
            )
        },
    ) {
        HDSelectedTabs(tabs, curTab, onClick)
    }
}

@Composable
internal fun DeviceDetailTabs(
    selected: HDDeviceTab,
    tabs: List<HDDeviceTab>,
    onClick: (HDDeviceTab) -> Unit,
) {
    //Row(Modifier.fillMaxWidth()) {
    Row(Modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        tabs.forEach { tab ->
            key(tab.key) {
                DeviceDetailTabV2(tab.options.title, selected = tab == selected) {
                    onClick(tab)
                }
            }
        }
    }
}

@Composable
internal fun RowScope.DeviceDetailTab(tab: String, selected: Boolean, onClick: () -> Unit) {
    Box(Modifier.weight(1f)) {
        SelectedHDDeviceTab(Modifier.fillMaxWidth().height(44.dp), tab, selected, onClick)
    }
}

@Composable
internal fun DeviceDetailTabV2(tab: String, selected: Boolean, onClick: () -> Unit) {
    Box(Modifier) {
        SelectedHDDeviceTab(Modifier.height(44.dp), tab, selected, onClick)
    }
}

@Composable
internal fun Top(
    device: DeviceAndStateViewData,
    modifier: Modifier = Modifier,
) {
    DeviceCommunicationIdAndModel(modifier, device.communicationId, device.model)
}

@Composable
internal fun MenuList(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onMenuClick: (MenuData) -> Unit,
) {
    val list: Array<MenuData> by remember {
        mutableStateOf(MenuData.entries.toTypedArray())
    }
    XPopContainer(onDismiss = {
        onDismiss()
    }) {
        CHItemShadowShape(elevation = 16.dp, modifier = modifier) {
            Column(
                modifier = Modifier
//                .size(300.dp)
//                .wrapContentWidth()
                    .width(180.dp)
//                .widthIn(max = 300.dp, min = 100.dp)
//                .wrapContentHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .hdBackground { ItemDefaults.itemBackground }
//                .padding(16.dp)
            ) {
                list.forEach {
                    MenuItem(it) {
                        onMenuClick(it)
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MenuItem(menuData: MenuData, onClick: () -> Unit) {
    Row(modifier = Modifier
        .height(49.dp)
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        HDImage(
            resource = menuData.iconRes,
//            painter = painterResource(menuData.icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = (menuData.text),
            fontSize = 14.sp,
            color = app_textColor_333333
        )

    }
}